# HoldableSwipeHandler
Open Source Library for Holdable ViewHolder in RecyclerView

## Gradle Setup

[![](https://jitpack.io/v/yeon-kyu/HoldableSwipeHandler.svg)](https://jitpack.io/#yeon-kyu/HoldableSwipeHandler)


```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.yeon-kyu.HoldableSwipeHandler:HoldableSwipeHandler:1.2.2'
}
```
#### Latest Version : 1.2.2

## ScreenShot Examples

<p align="left">
    <img src="https://github.com/yeon-kyu/HoldableSwipeHandler/blob/main/screenshots/iTunes_Gif.gif" width="30%"/>
    🌴🌴
    <img src="https://github.com/yeon-kyu/HoldableSwipeHandler/blob/main/screenshots/KuRing_Gif.gif" width="30%"/>
    🌴🌴
</p>

## How To Use in Activity/Fragment with RecyclerView
```kotlin
val yourRecyclerView: RecyclerView
val yourAdapter: RecyclerView.Adapter or ListAdapter ..

HoldableSwipeHandler.Builder(requireContext())
    .setOnRecyclerView(yourRecyclerView) // mandatory.
    .setSwipeButtonAction(object : SwipeButtonAction { // mandatory.
        override fun onClickFirstButton(absoluteAdapterPosition: Int) {
            val item = yourAdapter.currentList[absoluteAdapterPosition]
            viewModel.deletePushDB(item.articleId) // this is an example.
        }
    })
    .setBackgroundColor("#ff0000") // optional. default value is pink color
    .setFirstItemDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check)!!) // optional. default value is a 'trash can' icon
    .setFirstItemSideMarginDp(20) // optional. default value is 18. (in dip unit)
    .setDismissOnClickFirstItem(true) // optional. default value is 'true'
    .excludeFromHoldableViewHolder(10010) // optional. add ViewType that you want to exclude from holdable ViewHolder
    .build()
```

### 라이브러리 제작 및 배포 후기 블로그
- https://yk-coding-letter.tistory.com/28

## Version Updates
### v.1.2.2
  - 왼쪽 -> 오른쪽 스와이프도 가능하도록 변경
    - setDirectionAsRightToLeft() 파라미터에 false를 넣어서 적용 가능 (default값 : true)

### v.1.2.0
  - 라이브러리를 Builder Pattern으로 리팩토링
    - 기존의 기능들은 이전 방식(자바빈즈 패턴)으로도 사용 가능하나 권장하지 않음

### v.1.1.1
  - 지정한 ItemViewType에는 Swipe 불가능하게 가능하도록 수정
    - excludeFromHoldableViewHolder() 이용 가능
    - RecyclerView 내에서 아이템 종류에 따라 여러 ViewType을 나누었을 때 유용하게 사용 가능

### v.1.1.0
  - 버그 수정
    - 아이템 삭제되는 도중에 HoldingBackground가 종종 남아있는 버그 수정
    - 뷰홀더 배경이 투명한 경우에서의 UI 버그 수정(남아있는 투명 클릭 리스너 이슈)
    - 뷰홀더 자체의 클릭 리스너가 없는 경우에서의 버그 처리
  - 안정적인 버전 시작

### v.1.0.10
  - Holding 된 뷰홀더가 삭제되고 나서 해당 뷰홀더가 재활용될 때 translationX가 원복되지 않았던 이슈 수정

### v.1.0.9
  - HoldingBackground 의 버튼 클릭시 아이템을 사라지게 하는지에 대한 옵션
    - setDismissBackgroundOnClickFirstItem() 이용
      - default value : true

### v.1.0.8
  - 뷰홀더의 background가 투명한 경우 UI 버그 수정(뷰홀더 침범 이슈)

## License
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
