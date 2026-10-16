# Money Garden 🌿
### A Smart Personal Finance & Accounting Android App

**Money Garden** is an accounting-grade personal finance Android application designed to build lifelong financial discipline. It combines an engaging, gamified growing garden metaphor with a rigorous, professional double-entry bookkeeping ledger underneath, smart friend expense splitting, and Firebase Google Sign-In authentication.

---

## 📱 How to Download, Install & Update the APK

### Step 1: Download the APK
1. In the **Google AI Studio** project menu:
   - Click on the **Settings** or **Project Menu** (top-right area).
   - Choose **Download APK** or **Export Project** to download the latest compiled `app-debug.apk` directly to your computer or phone.
   - *Alternatively, if accessing via mobile browser, download the APK file directly to your phone's `Downloads` folder.*

### Step 2: Transfer to Your Android Device (if downloaded on PC)
- Transfer the downloaded `.apk` file to your phone via:
  - USB cable transfer to your device's `Download` folder.
  - Upload to Google Drive / OneDrive and download on your phone.
  - Send to yourself via Telegram / WhatsApp / Email.

### Step 3: Enable "Install Unknown Apps"
Because this is a standalone APK installed directly (not from Google Play Store):
1. Open your device's **Settings** ⚙️.
2. Search for **"Install unknown apps"** (or navigate to **Apps** → **Special app access** → **Install unknown apps**).
3. Select the app you are using to open the APK (e.g., **Files by Google**, **File Manager**, or **Chrome**).
4. Toggle **Allow from this source** to **ON**.

### Step 4: Install the App
1. Open your phone's **Files** or **Downloads** app.
2. Tap on the latest `app-debug.apk` file.
3. Tap **Install** (or **Update**).
4. If **Google Play Protect** displays a prompt saying *"Unrecognized app"* or *"Blocked by Play Protect"*:
   - Tap **"More details"** (or the small arrow dropdown).
   - Tap **"Install anyway"**.
5. Once complete, tap **Open** to launch **Money Garden**.

---

## ⚠️ Troubleshooting: Why Changes Might Not Be Effected (Fixes)

If you have downloaded and installed a new APK but the newest changes or authentication are not showing:

### 1. 🔄 Uninstall the Old App First (Most Common Fix)
When Android installs an updated debug APK, the operating system package manager may retain existing cached bytecode, compiled classes, or conflicting package signatures.
- **Fix:** 
  1. Long-press the **Money Garden** icon on your home screen or app drawer.
  2. Tap **App info** (or drag to **Uninstall**).
  3. Tap **Uninstall** and confirm.
  4. Now go to your **Downloads** folder and tap the newly downloaded APK to perform a clean, fresh installation.

### 2. 🧹 Clear Storage & App Cache
Android preserves local Room SQLite database files and cached settings across reinstalls if Google Cloud Auto-Backup is enabled on your phone.
- **Fix:**
  1. Go to **Settings** → **Apps** → **Money Garden**.
  2. Tap **Storage & cache**.
  3. Tap **Clear storage** (or **Clear data**) and **Clear cache**.
  4. Re-open the app.

### 3. 📦 Ensure You Are Installing the Right File
- Often devices store multiple files named `app-debug.apk`, `app-debug (1).apk`, `app-debug (2).apk` in the Downloads folder.
- **Fix:** Check the file creation time/date in your File Manager to ensure you tap the most recent download, or delete older APK files before downloading.

### 4. 🏷️ Verifying Version
- The latest build is set to **Version 1.4 (Build 5)** with `applicationId`: `com.aistudio.moneygarden.arptmg`.
- You can verify this in **Settings → Apps → Money Garden** (scroll to the bottom of the App Info page).

---

## 🌟 Key Highlights & Philosophy

1. **User Authentication & Google Sign-In:**
   - Seamless sign-in with your Google Account powered by Jetpack `CredentialManager` and Firebase Authentication.
   - Tap your user avatar in the top app bar to view account details, email, and sign out at any time.

2. **Accounting-Grade Accuracy:**
   - Every financial transaction posts balanced debits and credits (`sum(debit) == sum(credit)`).
   - All amounts are computed in integer minor units (**paise**, 1 INR = 100 paise) to eliminate floating-point rounding errors.
   - Beautiful Indian number formatting (`₹1,25,000.50`, `₹250.00`).
   - True auditable ledger: transactions are never deleted silently. Any correction is posted as a balanced reversal journal entry linked to the original.

3. **Smart Shared Expenses & Friend Splits:**
   - Equal splits, unequal splits, and multi-friend splits.
   - Clearly separates **personal expenditure** from **receivables owed by friends**.
   - Supports purchases where a friend pays for you (records a payable without touching cash or bank funds).
   - Repayments of receivables are treated as collection of assets (never artificial income).
   - Repayments of payables are treated as settlement of liabilities (never new expenses).
   - Clear human-friendly statements: *"Alex owes you ₹250.00"*, *"You owe Priya ₹180.00"*, *"You and Alex are settled up! ✨"*.

4. **The Virtual Money Garden & Positive Gamification:**
   - Custom-drawn 5-stage plant visualizer (Seed 🌰 → Tender Sprout 🌱 → Growing Bush 🌿 → Budding Flower 🌸 → Money Tree in Bloom 🌳).
   - Savings goals track real intentions within bank/cash funds without creating duplicate financial assets.
   - **"No-Spending Today" Check-in:** Users can check in to maintain consistency routines without fabricating fake financial transactions.
   - 10 Genuine, verifiable achievements: *First Seed*, *Getting Organised*, *Consistency Champion*, *Receipt Ready*, *Balance Checker*, *Settlement Star*, *Savings Starter*, *Goal Getter*, *Money Detective*, and *Financially Organised*.

5. **Explainable Money Health Score:**
   - 0–100 score based on recordkeeping habits, reconciliation, and split reviews—never penalizing low income or necessary expenses.
   - Actionable tips to water and nurture the garden.

6. **Financial Reports & Audit:**
   - Income & Personal Expenditure breakdown by category.
   - Complete Balance Sheet (`Assets = Liabilities + Equity`) mathematically verified.
   - Cash & Bank movement statement.
   - CSV Ledger Export with properly escaped quotes and headers.
   - Monthly and Weekly review wizard.
   - Account Balance Reconciliation wizard.

---

## 🏗️ Architecture & Technology Stack

- **Platform:** Native Android (Kotlin & Jetpack Compose).
- **Authentication:** Firebase Auth & Jetpack Credential Manager (Google Sign-In).
- **Architecture:** MVVM + Clean Architecture + Repository Pattern.
- **Database:** Room (SQLite) with transactional consistency (`@Transaction`), foreign keys, and reactive `Flow`.
- **Theming & Design:** Material Design 3 (M3) palette with soft mint green, deep teal, soft lavender, warm cream, and golden accents.
- **Testing:** Local JVM JUnit & Robolectric testing for CUJs and complete accounting rules.

---

## 🧪 Testing Verification

The project includes unit and Robolectric tests in `app/src/test/java/com/example/`:
- Personal expense balances debits and credits and reduces payment account.
- Shared expenses accurately divide personal expense from receivables.
- Friend-paid expenses credit payables without reducing bank funds.
- Unequal splits sum exactly to total purchase amounts.
- Reversals flip debits and credits and restore ledger balances.
- Repayments do not create artificial income or expense.
- Balance Sheet balances (`Assets == Liabilities + Equity`).
- Indian numbering handles lakhs and paise accurately.

To execute tests via Gradle:
```bash
gradle :app:testDebugUnitTest
```
