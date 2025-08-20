# MovieSocial App

MovieSocial is a modern movie application powered by the **TMDB API**.  
It provides users with an engaging movie discovery experience, personalized interactions, and unique animations that bring movies to life.

---

## Features

- **Home Screen**
  - Displays trending movies.
  - Favorite movies are highlighted with a green frame and a heart icon.
  - Users can add/remove movies from their favorites list easily.
  - Floating Action Button (FAB) navigates to the **Scratch Screen**, where users scratch a card to reveal a randomly selected movie.

- **Search Screen**
  - Search movies by keywords.

- **Favorite Screen**
  - View and manage favorite movies.
  - Remove movies from the list with a single tap.

- **Assistant Screen**
  - Integrated chatbot powered by **Gemini Free API**.
  - Users can chat about movies.
  - Assistant supports **function calls** to navigate directly to the **Favorite Screen** or **Search Screen**.

- **Settings Screen**
  - Customize the home page design.
  - Enable/disable animations.

---

## Interactive Animations (Movie Detail Screen)

- **Romantic** 
  Touching anywhere spawns animated heart icons.

- **Thriller**   
  The screen cracks wherever the user touches, creating a blurry effect.

- **Horror** 
  Initially, the screen is dark.  
  Users can turn on a virtual light bulb to reveal the content with a warm yellow glow.

---

## Technologies & Architecture

- **Networking**: Retrofit
  Error handling and coroutine support are implemented for better user experience.  

- **Architecture**: MVVM (Model-View-ViewModel)
  Clear separation of concerns.
  Easier maintainability and scalability.
  Improved testability.
  **ViewModels** manage UI-related data and handle business logic.
 
- **Dependency Injection**: Koin
  Koin is used for dependency injection to:
  Inject **ViewModels** into screens.
  Provide **Retrofit services** and **Repository** instances as singletons.
  Inject shared utility functions used across multiple ViewModels.
  This eliminates boilerplate code, reduces coupling, and improves testability.

- **UI/UX**: Material Design, Animations
- **Notifications**: Firebase
- **Coroutines** for asynchronous operations.


---

## Related medium writes are written by me:

**Creating a Dynamic Break Animation in Android Compose**: 
https://medium.com/@atliogluomer/how-to-create-a-break-animation-on-the-screen-android-compose-756068e122ba

**Using Function Calls to Enhance Chatbot Interactions**:
https://medium.com/@atliogluomer/using-function-calls-to-enhance-chatbot-interactions-b9a2020b078f

---

## 🚀 Installation

Clone the repository:

```bash
git clone https://github.com/Atlioglu/MovieSocialApp.git
```

And there is a qr code in the app, when anybody else scans it, the APK is reachable
