# Arpita’s Money Garden 🌿
### A Teen-Friendly Personal Finance & Accounting Android App

**Arpita’s Money Garden** is an accounting-grade personal finance Android application designed specifically to empower Arpita with lifelong financial discipline. It combines an engaging, gamified growing garden metaphor with a rigorous, professional double-entry bookkeeping ledger underneath.

---

## 🌟 Key Highlights & Philosophy

1. **Accounting-Grade Accuracy:**
   - Every financial transaction posts balanced debits and credits (`sum(debit) == sum(credit)`).
   - All amounts are computed in integer minor units (**paise**, 1 INR = 100 paise) to avoid floating-point inaccuracies.
   - Beautiful Indian number formatting (`₹1,25,000.50`, `₹250.00`).
   - True auditable ledger: transactions are never deleted silently. Any correction is posted as a balanced reversal journal entry linked to the original.

2. **Smart Shared Expenses & Friend Splits:**
   - Equal splits, unequal splits, and multi-friend splits.
   - Clearly separates **Arpita's personal expenditure** from **receivables owed by friends**.
   - Supports purchases where a friend pays for Arpita (records a payable without touching cash or bank funds).
   - Repayments of receivables are treated as collection of assets (never artificial income).
   - Repayments of payables are treated as settlement of liabilities (never new expenses).
   - Clear human-friendly statements: *"Alex owes you ₹250.00"*, *"You owe Priya ₹180.00"*, *"You and Alex are settled up! ✨"*.

3. **The Virtual Money Garden & Positive Gamification:**
   - Custom-drawn 5-stage plant visualizer (Seed 🌰 → Tender Sprout 🌱 → Growing Bush 🌿 → Budding Flower 🌸 → Money Tree in Bloom 🌳).
   - Savings goals track real intentions within bank/cash funds without creating duplicate financial assets.
   - **"No-Spending Today" Check-in:** Arpita can check in to maintain consistency routines without fabricating fake financial transactions.
   - 10 Genuine, verifiable achievements: *First Seed*, *Getting Organised*, *Consistency Champion*, *Receipt Ready*, *Balance Checker*, *Settlement Star*, *Savings Starter*, *Goal Getter*, *Money Detective*, and *Financially Organised*.

4. **Explainable Money Health Score:**
   - 0–100 score based on recordkeeping habits, reconciliation, and split reviews—never penalizing low income or necessary expenses.
   - Actionable tips to water and nurture the garden.

5. **Financial Reports & Audit:**
   - Income & Personal Expenditure breakdown by category.
   - Complete Balance Sheet (`Assets = Liabilities + Equity`) mathematically verified.
   - Cash & Bank movement statement.
   - CSV Ledger Export with properly escaped quotes and headers.
   - Monthly and Weekly review wizard.
   - Account Balance Reconciliation wizard.

---

## 🏗️ Architecture & Technology Stack

- **Platform:** Native Android (Kotlin & Jetpack Compose).
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
