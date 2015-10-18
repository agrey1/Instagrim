This project for AC32007 builds upon the code found at https://github.com/acobley/instagrim

A summary of the changes/additions can be found below. This is summary of this project's commit history.

All pages have very basic CSS.
Added log out functionality.
The logout page redirects users to the home page after logging out.
Login error messages added.
Added "Remember me" function to the login form using cookies.
Registration form validation and errors.
The registration form requires passwords to be entered twice.
web.xml changes added to permit /Instagrim/{username} URL pattern.
Image borders removed.
Thumbnails have a maximum and minimum size. The aspect ratio is maintained.
After uploading an image, the user is redirected to their profile page.
instagrim.pics table structure altered to support comments.
It is possible to post comments on a picture.
Only 5 comments are displayed per picture on the profile page.
All comments for an image can be seen at /Instagrim/Image/{imageid}/Comments
Comments are displayed in order of time posted.
Comments cannot be longer than 100 characters.
There is a real time indicator of your comment's length - implemented with JavaScript.
Validation is in place for usernames and passwords, etc.
Users must be logged in to perform actions such as uploading an image or posting a comment.
Internal server errors are handled by a custom "500" series error page - such as the database being offline.
Added a custom 404 not found page.
It is possible for users to search for other users.
Users can choose which image should be used as their profile picture/avatar.

The following URL patterns are now implemented:
/Instagrim - Home page/login page, will display your profile if you are logged in
/Instagrim/{username} - View the given user's profile (list of pictures with comments etc)
/Instagrim/Profile/{username} - Same as above but not references by URL
/Instagrim/Image/{imageid} - Gets the full sized image
/Instagirm/Image/{imageid}/Comments - View all comments for an image
/Instagrim/Thumb/{imageid} - Gets the thumbnail/preview of an image
/Instagrim/LogOut - Logs the user out if they were logged in and redirects to home
/Instagrim/Register - Account registration page
/Instagrim/Search - Displays search results (Search for users)
/Instagrim/Settings - Allows a user to change their account settings
/Instagrim/DeleteAccount - If logged in, the user will be logged out and their account will be deleted
