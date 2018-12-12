#!/bin/bash

# Need bash >= 4.2
# ------------ Need Run it First  ----------
# sudo usermod -aG sudo telegraf
# touch /var/log/file_status.info
# chmod 777 /var/log/file_status.info
# ------------ Need Run it First  ----------

# -------------- CONFIG START -------------
# path to find log, the same with filebeat
FIND_PATH="/usr/local/ipbx/IntraSwitch/http/out/"
# regex to find log, the same with filebeat
REGEX=".*.log"

# save this script files_status
# > --- metadata_file ---
# > file_path,file_size,file_inode,file_change_time(epoch)
# > ...
SAVE_METADATA_PATH="/opt/scripts/get_log_append_size.txt"

# -------------- CONFIG END -----------------
# -------------------------------------------


declare -A last_status_inode
declare -A last_status_size
declare -A last_status_date


RATE_OF_CHANGE="0"
# $1 file_path,file_size,file_inode,file_change_time(epoch)
# return RATE_OF_CHANGE
cal_file(){
  file_path=$(echo $1 | cut -f1 -d,)
  file_size=$(echo $1 | cut -f2 -d,)
  inode_id=$(echo $1 | cut -f3 -d,)
  created_at=$(echo $1 | cut -f4 -d,)

  # 1. if file not in the last files status
  #if ! [[ -v "last_status_inode[$file_path]" ]] ; then
  if [[ -z "${last_status_inode[$file_path]}" ]] ; then
    last_status_size[$file_path]=$file_size
    last_status_inode[$file_path]=$inode_id
    last_status_date[$file_path]=$created_at
    RATE_OF_CHANGE=0
    return
  fi

  # 2. if file rotate onec
  #if ! [ "$inode_id" -eq "${last_status_inode[$file_path]}" ] ; then
  if [ "$inode_id" -ne "${last_status_inode[$file_path]}" ] ; then

    #  (file.status.size) + (file.last_status.inode.get_file().size - file.last_status.size)
    #  > Last status:
    #    file_path:/log/a.log   inode:1   size:   10mb
    #  > Now status:
    #    file_path:/log/a.log   inode:2   size:   6mb
    #    file_path:/log/a.log.1 inode:1   size    20mb
    # file_size   last_file_current_size   last_status_size[$file_path]
    #  6mb   +            (20mb -                 10mb)
    #  6mb + (20mb - 10mb) is delta

    last_file_current_size=$(find $FIND_PATH  -inum ${last_status_inode[$file_path]} -printf "%s")
    let size_delta=$file_size+${last_file_current_size}-${last_status_size[$file_path]}
    if [[ "$created_at" != "${last_status_date[$file_path]}" ]]; then
      RATE_OF_CHANGE=$(echo $size_delta $created_at ${last_status_date[$file_path]}| awk '{ printf "%0.8f\n" ,$1/($2-$3)}')
    fi
    last_status_size[$file_path]=$file_size
    last_status_date[$file_path]=$created_at
    last_status_inode[$file_path]=$inode_id
    return
  fi

  # 3. normal
  let size_delta=$file_size-${last_status_size[$file_path]}
  if [[ "$created_at" != "${last_status_date[$file_path]}" ]]; then
    RATE_OF_CHANGE=$(echo $size_delta $created_at ${last_status_date[$file_path]}| awk '{ printf "%0.8f\n" ,$1/($2-$3)}')
  fi
  last_status_size[$file_path]=$file_size
  last_status_date[$file_path]=$created_at
  return
}

load(){
  if [ -f $SAVE_METADATA_PATH ]; then
    while read -r line; do
      file_path=$(echo $line | cut -f1 -d,)
      file_size=$(echo $line | cut -f2 -d,)
      inode_id=$(echo $line | cut -f3 -d,)
      created_at=$(echo $line | cut -f4 -d,)
      last_status_size[$file_path]=$file_size
      last_status_inode[$file_path]=$inode_id
      last_status_date[$file_path]=$created_at
    done < "$SAVE_METADATA_PATH"
  fi

}

save(){
  : > $SAVE_METADATA_PATH
  for i in "${!last_status_inode[@]}"
  do
    echo "$i,${last_status_size[$i]},${last_status_inode[$i]},${last_status_date[$i]}" >> $SAVE_METADATA_PATH
  done
}


craw(){
  total=0
  result=$(find $FIND_PATH -regextype sed -regex $REGEX -printf "%p,%s,%i,%C@,\n")
  for value in $result
  do
    cal_file $value
    total=$(echo $total $RATE_OF_CHANGE}| awk '{ printf "%0.8f\n" ,$1 + $2}')
  done
  # count: byte/second
  echo "${total}"
}
main(){

  load
  craw
  save
}

main
