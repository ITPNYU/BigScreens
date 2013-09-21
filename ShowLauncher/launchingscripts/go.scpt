on increment()
	global mycount
	set mycount to mycount + 1
end increment

# set one to "ssh -i ~/.ssh/bsleft bigscreens@192.168.130.240 './go.sh'"
# set two to "ssh -i ~/.ssh/bsmiddle bigscreens@192.168.130.241 './go.sh'"
# set three to "ssh -i ~/.ssh/bsright bigscreens@192.168.130.242 './go.sh'"
# set server to "ssh -i ~/.ssh/bsmiddle bigscreens@192.168.130.241 './mpeserver.sh'"

set one to "ssh -i ~/.ssh/bsleft bigscreens@192.168.130.240"
set two to "ssh -i ~/.ssh/bsmiddle bigscreens@192.168.130.241"
set three to "ssh -i ~/.ssh/bsright bigscreens@192.168.130.242"
set server1 to "ssh -i ~/.ssh/bsmiddle bigscreens@192.168.130.241"
set server2 to "ssh -i ~/.ssh/bsleft bigscreens@192.168.130.240"

set commands to {one, two, three}
set mycount to 0

repeat with cur in commands
	tell application "Terminal"
		activate
		with timeout of 1800 seconds
			do script with command cur
			tell window 1
				set number of columns to 65
				set number of rows to 24
				set the position to {mycount * 480, 20}
			end tell
		end timeout
	end tell
	increment()
end repeat

tell application "Terminal"
	activate
	with timeout of 1800 seconds
		do script with command server1
		tell window 1
			set number of columns to 70
			set number of rows to 24
			set the position to {2 * 480, 400}
		end tell
	end timeout
end tell

tell application "Terminal"
	activate
	with timeout of 1800 seconds
		do script with command server2
		tell window 1
			set number of columns to 65
			set number of rows to 24
			set the position to {1 * 480, 400}
		end tell
	end timeout
end tell
