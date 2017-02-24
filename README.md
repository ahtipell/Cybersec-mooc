# Cybersec-mooc
Helsinki Uni &amp; F-secure Cyber security Mooc Project 1

Nicely vulnerable web application.

First note: This document is easier to read in "raw view"-mode.

This repository is for Helsinki University CyberSec Mooc and contains code for extremely vulnerable web server application to exploit. Main functionality of the application contains a platform to sign up for "Bileet" (party in english) and super basic admin feature to print out the list of current participants.

The application contains a series of flaws and vulnerabilities to exploit. They're mainly commented inside the code, but outlined here as well:

Unvalidated user inputs. The user is allowed to pass html-tags in the sign up form, which are then executed by the admin browser when opening signups-page. Allows for script injections etc. To fix unvalitated inputs, change the form thymeleaf "th:utext" to "th:text", which enables tag escaping.

Multiple Security misconfigurations and access level problems. Implementor of the service has no idea of what to do, so there're some fuckups in the configs. HttpOnly-cookies are disabled, allowing the client browser to access session token. Session token length is limited and therefore predictable enough to allow session hijacking even without actually stealing the cookie. Some guy "ted" has user-priviliged access to the system for unknown reason and the admin account is left unconfigured. After any successful login, access to any page is "technically" allowed. Etc. Easy fixes: remove unsecure configs. Spring is pretty decent by the way it is, no need to hack the JSESSIONIDs or disable httpOnly-cookies.

Cross site requests are allowed, which of course may lead in misuse of the system, allowing session hijack attacks and other data leakages and logouts. Fix: enable csrf-security.

Insecure direct references. Any logged in user is allowed access to the sign ups data by simply providing a url parameter "admin=true" with the GET-request. The implementor hasn't understood the flow of access control and has made a bad call within. Fix: read the documents on how the session management and accesslevels work. Remove the url parameters as they're not needed.

Also, this "ted" has a weak password which indeed can be fuzz-attacked with a dictionary. ;) Fix: damn, check the user repository once in a while dude!

Happy hacking!

Edit:
I got some feedback from this project submit which criticizes the lack of the actual steps to reproduce the vulnerabilities embedded and the actual steps to fix the flaws. In my personal opinion, this project task (and especially reviews of these tasks) isn't about following preprinted footsteps on identification and fix process - it's about testing the skills you've learned so far.

Edit 2: I Stand Corrected. It appears, that the teachers of the course want a bit longer report than this is, so I shall step my game up a little!

Vulnerability 1: Misconfigured admin account
Steps to reproduce:
1. Run the Cybersec-mooc project server on local host
2. Open web browser, locate http://localhost:port/login page
3. Login as user "admin" with password "admin"
4. You should be redirected to signups.html admin panel
Steps to counter:
1. Change admin users password to a strong, not predictable one.

Vulnerability 2: Misconfigured session management
Steps to reproduce:
1. Run the Cybersec-mooc project server on local host
2. Open web browser, locate http://localhost:port/login page
3. Login as user "admin" with password "admin"
4. Using a different platform (not linked to your browsers cookies), run the python 2.7 script "session-hijack-scanner.py" located in Scripts-folder (in the base of this repository)
4.1 (The script brute forces http-get requests with custom JSESSIONIDs and breaks the loop when it finds response content lenght larger than the one of the failed request.)
5. By inspecting the lenght of the content returned by the loop, the script will inform you with a passable JSESSIONID and the response.content returned by the successful request.
Steps to counter:
1. From SignupsController.java, remove TomcatContextCustomizer Bean and it's associates
1.1 (Lines 66 - 86. These beans limit the JSESSIONID to 1 hex and allow it to be reached from the browser console.)
1.2 (Beans: public TomcatEmbeddedServletContainerFactory tomcatContainerFactory() and public TomcatContextCustomizer tomcatContextCustomizer()) 
