Challenge provided by Tokenlab for a Junior Mobile Developer position.

Description:
The personal of Tokenlab really likes movies. Thus they decided to create a REST API to provide information concerning the 20 best punctuated movies according to TMDB users. The challenge consists in building an app (Android or iOS) which can fetch the data from this API, present it in an accesible and simple way. The app should follow the usability patterns of the platform selected.

Requirements:
    • The project must be developed with native technologies of the selected platform (Java or Kotlin for Android, or Swift for iOS).
    • The app must have at least two screens. A list of movies must be shown within the first screen (with Image and Title, or just Image). Once the user selects a movie from the list, he/she is redirected to a Details screen, where further information is exhibited. It is not necessary to show all the information provided by the API.
    • The application must keep the user informed when it is downloading or processing the data.
    • Use some kind of software architecture (MVP, MVVM, MVC, Clean Architecture, etc).
    • All code must be sent to a public remote git repository
    • Use any library from a third party to make HTTP calls. Feel free to use as many libraries as you want in your project.
Bonus:
    • Provide some kind of error handling. What happens if the device doesn’t have internet connection, or the server doesn’t answer properly?
    • Provide some kind of local database (cache) of API’s information. After successfully downloading data the first time, the app must save it locally so that the user can visualize it offline if desired.

API Information: To get the list of movies, it is enough to make a GET HTTP request on the given endpoint. To obtain detailed information of a specific movie, the id should be added at the end of the same endpoint.
