#!/bin/bash


# ------------ Need Run it First  ----------
# sudo usermod -aG sudo telegraf
# touch /var/log/logsize.log
# chmod 777 /var/log/file_status.log
# ------------ Need Run it First  ----------

# -------------- CONFIG START -------------
# path to find log, the same with filebeat
FIND_PATH="/logs/"
# regex to find log, the same with filebeat
REGEX=".*.log"
# save files status
SAVE_PATH="/var/log/file_status.info"
# -------------- CONFIG END ------

TMP="0"
declare -A inode_map
declare -A size_map
# [optional]
declare -A date_map

# $1 file_path
# $2 file_size bytes
# $3 file_inode_numb
# $4 file_change_epoch
cal_file(){
  # IFS=, read PATH SIZE INODE DATE<<< "$1"
  file_path=$(echo $1 | cut -f1 -d,)
  file_size=$(echo $1 | cut -f2 -d,)
  inode_id=$(echo $1 | cut -f3 -d,)
  created_at=$(echo $1 | cut -f4 -d,)

  if ! [[ -v "inode_map[$file_path]" ]] ; then
    size_map[$file_path]=$file_size
    inode_map[$file_path]=$inode_id
    date_map[$file_path]=$created_at
    TMP=0
    return
  fi

  # if file rotate
  if ! [ "$inode_id" -eq "${inode_map[$file_path]}" ] ; then
    max_rotate=$(find $FIND_PATH  -inum ${inode_map[$file_path]} -printf "%s")
    # echo "debug $file_size $max_rotate $file_path ${size_map[$file_path]}"
    # echo ">> $(expr "$file_size + ${max_rotate} - ${size_map[$file_path]}")"
    # TMP=$(expr "$file_size + ${max_rotate} - ${size_map[$file_path]}")
    # TMP=($file_size+${max_rotate}-${size_map[$file_path]})/($created_at-date_map[$file_path])
    let filesize=$file_size+${max_rotate}-${size_map[$file_path]}
    TMP=$(echo $filesize $created_at ${date_map[$file_path]}| awk '{ printf "%0.8f\n" ,$1/($2-$3)}')
    size_map[$file_path]=$file_size
    date_map[$file_path]=$created_at
    inode_map[$file_path]=$inode_id
    return
  fi
  #
  # echo "debug $file_size $file_path ${size_map[$file_path]}"
  # echo ">> $(expr "$file_size - ${size_map[$file_path]}")"
  # TMP=$(expr "$file_size - ${size_map[$file_path]}")
  # TMP=($file_size-${size_map[$file_path]})/($created_at-date_map[$file_path])
  let filesize=$file_size-${size_map[$file_path]}
  TMP=$(echo $filesize $created_at ${date_map[$file_path]}| awk '{ printf "%0.8f\n" ,$1/($2-$3)}')
  size_map[$file_path]=$file_size
  date_map[$file_path]=$created_at
  return
}

load(){
  if [ -f $SAVE_PATH ]; then
    while read -r line; do
      file_path=$(echo $line | cut -f1 -d,)
      file_size=$(echo $line | cut -f2 -d,)
      inode_id=$(echo $line | cut -f3 -d,)
      created_at=$(echo $line | cut -f4 -d,)
      size_map[$file_path]=$file_size
      inode_map[$file_path]=$inode_id
      date_map[$file_path]=$created_at
    done < "$SAVE_PATH"
  fi

}

save(){
  : > $SAVE_PATH
  for i in "${!inode_map[@]}"
  do
    echo "$i,${size_map[$i]},${inode_map[$i]},${date_map[$i]}" >> $SAVE_PATH
  done
}


craw(){
  total=0
  result=$(find $FIND_PATH -regextype sed -regex $REGEX -mmin -.016 -printf "%p,%s,%i,%C@,\n")
  for value in $result
  do
    cal_file $value
    total=$(echo $total $TMP}| awk '{ printf "%0.8f\n" ,$1 + $2}')
  done
  echo "${total}"
}
main(){

  load
  craw
  save
}

main
