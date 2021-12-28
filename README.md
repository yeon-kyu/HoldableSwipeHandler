# HoldableSwipeHandler
Open Source Library for Holdable ViewHolder in RecyclerView

### Gradle Setup

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.yeon-kyu.HoldableSwipeHandler:HoldableSwipeHandler:1.0.4'
}
```
##### Latest Version : 1.0.4

### Examples
- comming soon

### How To Use in Activity/Fragment with RecyclerView
```kotlin
val yourRecyclerView : RecyclerView
val yourAdapter : RecyclerView.Adapter or ListAdapter ..

val swipeHelper = HoldableSwipeHelper(context, object : SwipeButtonAction {
  override fun onClickFirstButton(absoluteAdapterPosition: Int) {
    // do something if you want to get callback when 'holded' button is clicked
    viewModel.deleteNotification(yourAdapter.currentList[absoluteAdapterPosition].articleId)
  }
})

swipeHelper.apply {
  setBackgroundColor("#000000") // not necessary. default value is pink color 
  setFirstItemDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check)!!) // not necessary. default value is a 'trash can' icon
  setFirstItemSideMarginDp(20) // not necessary. default value is 18. (in dip unit)
}

// the codes bellow are necessary
swipeHelper.addRecyclerViewListener(yourRecyclerview)
swipeHelper.addRecyclerViewDecoration(yourRecyclerview)
val itemTouchHelper = ItemTouchHelper(swipeHelper)
itemTouchHelper.attachToRecyclerView(yourRecyclerview)
```

### About this
Currently, just one 'Holded' Button is supported, but I will manage to support two or more items in the future. 

Any Feedback or PR would be appreciated. Thank you.

### License
```xml
Designed and developed by 2021 yeon-kyu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
