# 📰 NewsPulse

**NewsPulse** is a modern Android application that delivers the latest news headlines in a clean and interactive UI. Users can explore trending articles, read full stories, open external news links, and save articles for offline reading.

---

## ✨ Features

- 🔍 **Browse News Headlines**  
  Fetches and displays a list of current news articles using a public news API.

- 📖 **Detailed Article View**  
  Tap on any headline to see the full article description in a clean layout.

- 🌐 **Open Full Article in Browser**  
  Each article contains a link to the source website, allowing users to read the original article.

- 💾 **Offline Reading Support**  
  Save your favorite articles and read them even without an internet connection.

- 🔄 **Swipe to Refresh**  
  Easily refresh the news feed with a pull-down gesture.

- ❤️ **Favorites Management**  
  Mark and manage saved articles with a single tap.

- ⚡ **Shimmer Loading Animation**  
  Beautiful placeholder animation while data is loading.

## 🧠 Architecture & Design Choices

NewsPulse follows the **MVVM (Model–View–ViewModel)** architecture to ensure a clear separation of concerns, better testability, and a responsive UI.

### 🏛 MVVM Architecture
- **Model**: Represents the data layer, including data classes and Room database entities.
- **ViewModel**: Acts as a bridge between the UI and data sources. Holds UI-related logic and exposes `LiveData` to the UI.
- **View (Activity/Fragment)**: Observes the `ViewModel`, renders UI, and handles user interaction.

> This architecture allows the UI to remain reactive and lifecycle-aware while keeping logic testable and loosely coupled.

---

### 🧩 Why These Libraries & Tools?

| Tool / Component        | Reason for Use                                                                 |
|-------------------------|--------------------------------------------------------------------------------|
| **ViewModel**           | Stores UI-related data that survives configuration changes (e.g., rotation).  |
| **LiveData**            | Observes changes in data and updates the UI automatically.                    |
| **DataBinding**         | Binds UI components in layouts directly to data sources in the ViewModel.     |
| **Room DB**             | Provides a local SQLite abstraction to store favorite/offline articles.       |
| **Retrofit**            | Simplifies network requests and parsing API responses.                        |
| **Glide**               | Efficient image loading and caching.                                          |
| **Material Components** | For modern, consistent UI design that aligns with Material Design guidelines. |
| **SwipeRefreshLayout**  | Adds pull-to-refresh gesture easily.                                          |
| **Shimmer Effect**      | Provides a better user experience while content is loading.                   |

---

### 🧵 Threading & Performance

- Used **Kotlin Coroutines** for asynchronous tasks (network and DB operations).
- All long-running operations run on the **IO Dispatcher** to avoid blocking the main thread.

---

### 🔐 Clean Code Principles

- Separated concerns into layers: API calls, database access, and UI.
- Used **constants** in a centralized `ConstantValues` object for reusability.
- Followed naming conventions and modularization for better maintainability.

---

### ✅ Null-Safety with Kotlin

- Used **Elvis operator (`?:`)** extensively to handle nullable values.
- Ensures that if any field (like title, description, etc.) is `null`, the app substitutes a safe default or skips processing.
  
---
## 🎬 App Demo

Click below to view the demo videos of the NewsPulse app in action:

- ▶️ [Demo 1 – App Launch & News Feed](./show_news_list.mp4)
- ▶️ [Demo 2 – Detailed Articles & open article in browser](./show_news_description.mp4)
- ▶️ [Demo 2 – Saved Articles](./show_saved_articles.mp4)

## 🏗️ Setup Instructions

1. **Clone the repo**
   ```bash
   git clone https://github.com/MaheshVhatkar/NewsPulse.git
