package ru.geowork.photoapp.ui.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
class AppColors(
    val material: Colors,

    //content: Используются для стилизации контента (Текст, Иконки и т.п)
    val contentConstant: Color,     //Одинаковый в темной и светлой теме. Используется в тексте акцентных кнопок
    val contentPrimary: Color,      //Для основных элементов
    val contentSubPrimary: Color,   //Для вспомогательных элементов
    val contentSecondary: Color,    //Для вспомогательных элементов
    val contentDisabled: Color,     //Для заблокированных элементов
    val contentBorder: Color,       //Для обводок
    val contentBackground: Color,   //Для подложек

    //accent: Используются для стилизации акцентных элементов (Кнопки, Активные состояния и т.п)
    val accentPrimary: Color,       //Для основных элементов
    val accentSubPrimary: Color,    //Для вспомогательных элементов
    val accentSecondary: Color,     //Для вспомогательных элементов
    val accentDisabled: Color,      //Для заблокированных элементов
    val accentBorder: Color,        //Для обводок
    val accentBackground: Color,    //Для подложек

    //background: Используются для стилизации фоновых слоев (Области на которых расположен контент)
    val backgroundPrimary: Color,   //Нижний слой, на котором размещается все остальное
    val backgroundSecondary: Color, //Второй слой, для размещения навигации
    val backgroundModal: Color,     //Третий слой, для попапов, тостов, тултипов и других всплывающих элементов
    val overlayDark: Color,         //Подложка, которая затемняет фон. Контент может размещаться прямо на ней
    val overlayLight: Color,        //Подложка, которая затемняет фон. Для размещения контента нужно добавить фон

    //colors: Используются для стилизации наборов, которые нужно отделить друг от друга цветом (События календаря и т.п.)
    val red: Color,                 //Красный
    val green: Color,               //Зеленый
    val purple: Color,              //Фиолетовый
    val orange: Color,              //Оранжевый
    val blue: Color                 //Синий
)
