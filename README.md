# news-mvvm

#### Android news application using Jetpack and MVVM architecture    

![Architecture diagram](https://developer.android.google.cn/topic/libraries/architecture/images/final-architecture.png)



```properties
#  Maybe need to add this file ：   local.properties   
#  Location of the SDK. This is only used by Gradle.

sdk.dir=C\:\\Users\\XXX\\AppData\\Local\\Android\\Sdk
```


## Difference

| news                             | news-mvvm                             |
|-|-|
| https://github.com/hjzf/news.git | https://github.com/hjzf/news-mvvm.git |
| OKhttp                           | Retrofit                            |
| LitePal                          | Room                                |
| BottomNavigationView + ViewPager | BottomNavigationView + navigationUI |
| findViewById                     | View Binding                        |
| ------------------               | ViewModel + LiveData                |
| ------------------               | Coroutines                          |
| RecyclerView.Adapter             | ListAdapter                         |
| TabLayout + ViewPager            | TabLayout + ViewPager2              |

## What will be added? 

**Paging**

