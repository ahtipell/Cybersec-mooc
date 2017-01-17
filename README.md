# Cybersec-mooc
Helsinki Uni &amp; F-secure Cyber security Mooc Project 1

Nicely vulnerable web application.

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
