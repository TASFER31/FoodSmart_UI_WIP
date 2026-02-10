🍎 FoodSmart App - Developer Documentation
Project: Food Expiration Tracker
Version: 1.0
Last Updated: February 2026
Front-End Developer: [Your Name]
Status: UI Complete ✅ | Database Integration Needed ⏳

📑 Table of Contents

Project Overview
App Structure
Panel 1: Welcome Screen
Panel 2: Main Inventory
Panel 3: Edit Item
Panel 4: Reports & History
Navigation Flow
Database Integration Guide
Functions to Implement
Testing Checklist


🎯 Project Overview
Purpose: Help users track grocery items and prevent food waste by monitoring expiration dates.
Key Features:

Track food items with expiry dates
Set reminders for expiring items
Calculate money wasted vs. saved
View expense reports by month
Track activity history

Tech Stack:

Language: Kotlin
UI: XML Layouts
Architecture: Activities (4 panels)
Database: 


🏗️ App Structure
File Organization
app/src/main/
├── java/com/example/foodsmart_ui_overview/
│   ├── MainActivity.kt              (Panel 1: Welcome)
│   ├── Stats_cards.kt               (Panel 2: Main Inventory)
│   ├── edit_item.kt                 (Panel 3: Add/Edit Item)
│   └── ReportsHistoryActivity.kt    (Panel 4: Reports)
│
└── res/layout/
    ├── activity_main.xml             (Panel 1 UI)
    ├── activity_stats_cards.xml      (Panel 2 UI)
    ├── activity_edit_item.xml        (Panel 3 UI)
    └── activity_expense_report.xml   (Panel 4 UI)

📱 Panel 1: Welcome Screen
File: MainActivity.kt + activity_main.xml
Purpose
First screen users see when opening the app. Simple introduction with branding.
UI Components

App title: "FoodSmart"
Tagline: "Smart planning, zero waste"
Description: "Never waste groceries again"
"Get Started" Button → Navigates to Panel 2
Background image (customizable)


Status
✅ Complete - No database integration needed

-----------------------------------------------------


📊 Panel 2: Main Inventory
File: Stats_cards.kt + activity_stats_cards.xml
Purpose
Main dashboard showing all food items and statistics.
UI Components
Top Section

🔍 SearchView - Search groceries
📜 History Button (top right) → Navigates to Panel 4

- Stats Cards 
- Items List (Scrollable)

Each item should be clickable → Opens Panel 3 for editing

at the Bottom there is a:
➕ "Add Item" FAB (Floating Action Button) → Opens Panel 3 to add new item

XML Layout Structure
ConstraintLayout (Root)
├── SearchView
├── Button (History)
├── LinearLayout (Stats cards - horizontal)
│   ├── LinearLayout (Card 1)
│   ├── LinearLayout (Card 2)
│   └── LinearLayout (Card 3)
├── ScrollView ← IMPORTANT: Only ONE child allowed
│   └── LinearLayout (Items container - vertical)
│       ├── TextView (Item 1)
│       ├── TextView (Item 2)
│       └── ... (more items)
└── ExtendedFloatingActionButton (Add Item)

Status
✅ UI Complete
⏳ Needs Database Integration (see Functions to Implement section)
Current Functionality

✅ Search filter (hides/shows items visually)
✅ Navigation to Panel 3 (add/edit)
✅ Navigation to Panel 4 (history)

Needs Implementation

❌ Load items from database dynamically
❌ Update stats cards with real counts
❌ Refresh list when returning from Panel 3
❌ Color coding based on expiry status

----------------------------------------------------

✏️ Panel 3: Edit Item
File: edit_item.kt + activity_edit_item.xml
Purpose
Add new items or edit existing items with all details.
UI Components (Top to Bottom)

← Back Button - Cancel and return to Panel 2
Title: "Item Details"
Item Name (📦 icon)

EditText field
User can type item name


Expiry Date (📅 icon)
Display field showing date
✏️ Edit button → Opens calendar picker


Reminder (🔔 icon)
Display field showing reminder date or "None"
✏️ Edit button → Opens calendar picker


Price (💵 icon) ← NEW FIELD

EditText field
Input type: decimal number
Placeholder: "Price (e.g., $3.99)"


Inventory (🏠 icon)
- Spinner/Dropdown with options:
- Refrigerator
- Freezer
- Pantry
- Cabinet
- Countertop
- Other



Storage (📍 icon)
- EditText field
- Specific location (e.g., "Top shelf")


Amount (⚖️ icon)
- EditText field
- Example: "500g", "2 packs"


Category (🗂️ icon)
Spinner/Dropdown with options:

Meat
Dairy
Frozen Goods
Bakery
Vegetables
Fruits
Beverages
Other



Quantity (🔢 icon)
- Display number
- ➕ Button to increment

Created Date (📅 icon)

- Display only (not editable)
- Shows when item was first added

- "Save Changes" Button - Saves and returns to Panel 2

- Code Structure
- kotlinclass edit_item : AppCompatActivity() {


Status
✅ UI Complete
✅ Validation Complete
⏳ Needs Database Integration
Current Functionality

✅ Back button (cancel and return)
✅ Calendar pickers (expiry and reminder)
✅ Dropdowns (inventory and category)
✅ Quantity increment
✅ Input validation (name and price required)
✅ Save navigation (returns to Panel 2)

Needs Implementation

❌ Save item data to database
❌ Load existing item data for editing
❌ Pass item ID when clicking from Panel 2
❌ Quantity decrement button (optional)


-----------------------------------------------------------
📈 Panel 4: Reports & History
File: ReportsHistoryActivity.kt + activity_expense_report.xml
Purpose
View monthly expense summaries and activity history.
UI Components (Top to Bottom)
Header

← Back Button - Return to Panel 2
Title: "📊 Reports & History"

Expense Summary Section
📅 Month Selector - Dropdown to choose month
Summary Cards (2 cards side-by-side):

❌ Money Wasted: (from expired items)
✅ Money Saved: (items used before expiry)


Stats Row:
Total Items: 5
Expired Items: 1



Expired Items Section
Title: "❌ EXPIRED ITEMS (Wasted Money)"
List of expired items:

🌭 Hotdog - Frozen, 9 items - $53.91 - "Expired"
Shows: emoji, name, category, quantity, price, status



Active Items Section
Title: "✅ ACTIVE ITEMS (Still Fresh)"
List of fresh items:


Recent History Section
Title: "📜 RECENT HISTORY"
Timeline of actions



XML Layout Structure
ConstraintLayout (Root)
├── ImageButton (Back)
├── TextView (Title)
└── ScrollView ← IMPORTANT: Only ONE child
    └── LinearLayout (Content container - vertical)
        ├── TextView (Section: "EXPENSE SUMMARY")
        ├── LinearLayout (Month selector)
        ├── LinearLayout (Summary cards - horizontal)
        │   ├── LinearLayout (Money Wasted card)
        │   └── LinearLayout (Money Saved card)
        ├── LinearLayout (Total stats row)
        ├── View (Divider)
        ├── TextView (Section: "EXPIRED ITEMS")
        ├── LinearLayout (Expired items list)
        ├── View (Divider)
        ├── TextView (Section: "ACTIVE ITEMS")
        ├── LinearLayout (Active items list)
        ├── View (Divider)
        ├── TextView (Section: "RECENT HISTORY")
        └── LinearLayout (History timeline)
        
Status
✅ UI Complete
⏳ Needs Database Integration
Current Functionality

✅ Back button (return to Panel 2)
✅ Month selector dropdown
✅ Sample data display

Needs Implementation

❌ Load expense data from database by month
❌ Load expired items list dynamically
❌ Load active items list dynamically
❌ Load history timeline from database
❌ Calculate totals automatically
❌ Filter data when month is changed
