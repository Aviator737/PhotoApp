package ru.geowork.photoapp.ui.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0, 116, 223)

//Light colors
//content: Используются для стилизации контента (Текст, Иконки и т.п)
val ContentConstant = Color(255, 255, 255)          //Одинаковый в темной и светлой теме. Используется в тексте акцентных кнопок
val ContentPrimaryLight = Color(33, 37, 41)        //Для основных элементов
val ContentSubPrimaryLight = Color(68, 70, 71)   //Для вспомогательных элементов
val ContentSecondaryLight = Color(118, 120, 122)    //Для вспомогательных элементов
val ContentDisabledLight = Color(188, 191, 194)     //Для заблокированных элементов
val ContentBorderLight = Color(210, 212, 214)       //Для обводок
val ContentBackgroundLight = Color(233, 234, 235)   //Для подложек

//accent: Используются для стилизации акцентных элементов (Кнопки, Активные состояния и т.п)
val AccentPrimaryLight = Color(0, 116, 223)      //Для основных элементов
val AccentSubPrimaryLight = Color(80, 152, 242)    //Для вспомогательных элементов
val AccentSecondaryLight = Color(119, 174, 242)     //Для вспомогательных элементов
val AccentDisabledLight = Color(159, 197, 245)      //Для заблокированных элементов
val AccentBorderLight = Color(203, 223, 247)        //Для обводок
val AccentBackgroundLight = Color(230, 239, 250)    //Для подложек

//system: Используются для стилизации характера системны сообщений (ошибка, успех, предупреждение)
val SystemSuccessPrimaryLight = Color(36, 206, 159)  //Стилизация состояния успеха
val SystemSuccessBackgroundLight = Color(210, 250, 239) //Для подложек
val SystemErrorPrimaryLight = Color(242, 42, 65)   //Стилизация состояния ошибки
val SystemErrorBackgroundLight = Color(255, 214, 219) //Для подложек
val SystemWarningPrimaryLight = Color (242, 179, 37)  //Стилизация состояния предупреждения
val SystemWarningBackgroundLight = Color(252, 240, 212)//Для подложек

//background: Используются для стилизации фоновых слоев (Области на которых расположен контент)
val BackgroundPrimaryLight = Color(255, 255, 255)   //Нижний слой, на котором размещается все остальное
val BackgroundSecondaryLight = Color(247, 248, 250) //Второй слой, для размещения навигации
val BackgroundModalLight = Color(255, 255, 255)     //Третий слой, для попапов, тостов, тултипов и других всплывающих элементов
val OverlayDarkLight = Color(255, 255, 255)         //Подложка, которая затемняет фон. Контент может размещаться прямо на ней
val OverlayLightLight = Color(251, 251, 251)        //Подложка, которая затемняет фон. Для размещения контента нужно добавить фон

//Dark colors
val ContentPrimaryDark = Color(255, 255, 255)      //Для основных элементов
val ContentSubPrimaryDark = Color(194, 194, 194)   //Для вспомогательных элементов
val ContentSecondaryDark = Color(143, 143, 143)    //Для вспомогательных элементов
val ContentDisabledDark = Color(71, 71, 71)     //Для заблокированных элементов
val ContentBorderDark = Color(51, 51, 51)       //Для обводок
val ContentBackgroundDark = Color(41, 41, 41)   //Для подложек

//accent: Используются для стилизации акцентных элементов (Кнопки, Активные состояния и т.п)
val AccentPrimaryDark = Color(0, 116, 223)         //Для основных элементов
val AccentSubPrimaryDark = Color(119, 119, 217)    //Для вспомогательных элементов
val AccentSecondaryDark = Color(98, 98, 179)     //Для вспомогательных элементов
val AccentDisabledDark = Color(70, 70, 128)      //Для заблокированных элементов
val AccentBorderDark = Color(56, 56, 102)       //Для обводок
val AccentBackgroundDark = Color(42, 42, 77)    //Для подложек

//system: Используются для стилизации характера системны сообщений (ошибка, успех, предупреждение)
val SystemSuccessPrimaryDark = Color(36, 206, 159)  //Стилизация состояния успеха
val SystemSuccessBackgroundDark = Color(8, 51, 39) //Для подложек
val SystemErrorPrimaryDark = Color(242, 42, 65)   //Стилизация состояния ошибки
val SystemErrorBackgroundDark = Color(56, 9, 14) //Для подложек
val SystemWarningPrimaryDark = Color (242, 179, 37)  //Стилизация состояния предупреждения
val SystemWarningBackgroundDark = Color(51, 38, 8)//Для подложек

//background: Используются для стилизации фоновых слоев (Области на которых расположен контент)
val BackgroundPrimaryDark = Color(5, 5, 5)   //Нижний слой, на котором размещается все остальное
val BackgroundSecondaryDark = Color(20, 20, 20) //Второй слой, для размещения навигации
val BackgroundModalDark = Color(36, 36, 36)     //Третий слой, для попапов, тостов, тултипов и других всплывающих элементов
val OverlayDarkDark = Color(5, 5, 5)        //Подложка, которая затемняет фон. Контент может размещаться прямо на ней
val OverlayLightDark = Color(20, 20, 20)        //Подложка, которая затемняет фон. Для размещения контента нужно добавить фон

//colors: Используются для стилизации наборов, которые нужно отделить друг от друга цветом (События календаря и т.п.)
val Red = Color(243, 66, 87)                        //Красный
val Green = Color(36, 206, 159)                     //Зеленый
val Purple = Color(140, 140, 255)                   //Фиолетовый
val Orange = Color(243, 189, 61)                    //Оранжевый
val Blue = Color(42, 131, 242)                      //Синий
