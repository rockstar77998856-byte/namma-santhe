## 🧾 Namma Santhe Ledger – Android App

A simple and efficient Android application built for small vendors and shop owners to manage daily transactions digitally. The app focuses on clean design, fast performance, and easy record keeping.

---

## 🚀 Features

* 📱 **Clean and simple UI**: Focused on ease of use for small business owners.
* 🔐 **User login system**: Secure access to your ledger data.
* ➕ **Add customer transactions**: Easily record "Lend" and "Repay" actions.
* 📊 **Transaction history tracking**: View all past records in a clear list.
* ⏰ **Weekly reminder notifications**: Never forget to follow up on pending balances.
* 👤 **Profile management**: Customize your shop name and details.
* ⚙️ **Settings management**: Control app preferences.

---

## 🛠️ Tech Stack

* **Language**: Kotlin
* **Architecture**: MVVM (Model-View-ViewModel)
* **UI**: XML Layouts
* **Background Tasks**: WorkManager
* **Database**: Room Database (Local Storage)
* **Notifications**: Broadcast Receiver
* **Preferences**: Shared Preferences

---

## 📂 Project Structure

`com.example.nammasantheledger`

* `ui/` → Activities and UI logic (MainActivity, ProfileActivity, etc.)
* `data/` → Room Database entities, DAOs, and AppDatabase handling
* `adapter/` → List adapters for displaying transactions
* `utils/` → Helper classes and formatters

---

## ⚙️ Setup Instructions

### Clone the Repository

```bash
git clone https://github.com/rockstar77998856-byte/namma-santhe.git

```

---

### Or Download ZIP

1. Go to the repository on [GitHub](https://github.com/rockstar77998856-byte/namma-santhe.git)
2. Click **Code**
3. Click **Download ZIP**
4. Locate the downloaded ZIP file and **Extract** it.
5. Open the extracted folder.

---

### Import into Android Studio

1. Open **Android Studio**.
2. Click **File > Open**.
3. Select the `namma-santhe` project folder.
4. Wait for **Gradle sync** to complete.
5. Click **Run** (Green Play button) to launch the app on your device or emulator.

---

## 📦 Modules

* **LoginActivity**: Handles secure user authentication.
* **MainActivity**: The core dashboard for managing and adjusting transactions.
* **ProfileActivity**: Allows users to update shop names and profile info.
* **SettingsActivity**: Provides options for app configuration and logout.
* **ReminderReceiver**: Triggers the system notifications for reminders.
* **WeeklyReminderWorker**: Manages the scheduling of tasks via WorkManager.

---

## 🎯 Purpose

This app was designed to help small business owners move away from physical notebooks. By digitizing records, users can calculate balances instantly, reduce errors, and keep their financial data organized in one place.

---

## 🔮 Future Improvements

* **Firebase Integration**: Real-time data syncing.
* **Cloud Backup**: Never lose your ledger data.
* **PDF Report Export**: Generate monthly summaries for customers.
* **Search and Filter**: Quickly find specific customers or dates.
* **Dark Mode**: For better usability in low-light environments.
* **Multi-language Support**: Making the app accessible in regional languages.

---

## 🤝 Contributing

1. **Fork** the repository.
2. Create a **new branch** (`git checkout -b feature-improvement`).
3. **Commit** your changes.
4. **Push** to the branch and create a **Pull Request**.

---

## 📄 License

MIT License

---

## 👨‍💻 Author

Developed by **rockstar77998856-byte**

---

## ⭐ Support

If you find this project useful, give it a star on [GitHub](https://github.com/rockstar77998856-byte/namma-santhe.git)!