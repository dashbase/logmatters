import re
import subprocess

p = subprocess.Popen('ps -ef | grep java', shell=True, stdout=subprocess.PIPE)
out, err = p.communicate()
re_identifier = re.compile(r'.*identifier=(\d+).*')
re_total = re.compile(r'.*total=(\d+).*')
re_throttlenpersec = re.compile(r'.*throttleNPerSec=(\d+).*')
outs = out.decode('utf-8').split('\n')

# identifier list
identifiers = {}

if __name__ == '__main__':
    for line in outs:
        r = re_identifier.match(line)
        if r:  # matched
            identifier = r.group(1)
            if identifier not in identifiers:
                total = re_total.match(line).group(1)
                throttle = re_throttlenpersec.match(line)
                if throttle:
                    identifiers[r.group(1)] = {"throttle": throttle.group(1), "total": total}
                else:
                    identifiers[r.group(1)] = {"throttle": -1, "total": total}

    for identifier in identifiers:
        print(
            "Producer group {} have {} producers with throttle {}.".format(identifier, identifiers[identifier]['total'],
                                                                           identifiers[identifier]['throttle']))
    print("You can kill group by `./bin/kill.sh <group-number>`, or use `./bin/kill.sh` to kill them all.")
    print("Use `ps -ef | grep java` to get all running processed.")
