package com.wardrobeapp.server.data.db

import com.wardrobeapp.server.data.db.tables.ActivityTable
import com.wardrobeapp.server.data.db.tables.CategoryGroupTable
import com.wardrobeapp.server.data.db.tables.CategoryTable
import com.wardrobeapp.server.data.db.tables.ClimateTable
import com.wardrobeapp.server.data.db.tables.ColorTable
import com.wardrobeapp.server.data.db.tables.LabelTable
import com.wardrobeapp.server.data.db.tables.LuggageTypeTable
import com.wardrobeapp.server.data.db.tables.MaterialTable
import com.wardrobeapp.server.data.db.tables.SeasonTable
import com.wardrobeapp.server.data.db.tables.StyleTable
import com.wardrobeapp.server.data.db.tables.TripTypeTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseSeeder {

    fun seed() {
        transaction {
            seedCategoryGroups()
            seedSeasons()
            seedColors()
            seedMaterials()
            seedStyles()
            seedTripTypes()
            seedClimates()
            seedActivities()
            seedLuggageTypes()
            seedLabels()
        }
    }

    private fun seedCategoryGroups() {
        if (CategoryGroupTable.selectAll().count() > 0) return

        val groups = listOf(
            "Верхняя одежда" to listOf("Пальто", "Куртка", "Пуховик", "Плащ", "Ветровка", "Дубленка", "Шуба"),
            "Верх" to listOf("Футболка", "Поло", "Топ", "Рубашка", "Блузка", "Лонгслив", "Боди", "Свитер", "Кардиган", "Худи", "Пиджак", "Жилет", "Жакет"),
            "Низ" to listOf("Брюки", "Джинсы", "Юбка", "Шорты", "Леггинсы", "Спортивные штаны"),
            "Платье" to listOf("Платье"),
            "Костюмы и комплекты" to listOf("Комбинезон", "Спортивный костюм", "Пижама", "Классический костюм"),
            "Обувь" to listOf("Туфли", "Кроссовки", "Сандалии и босоножки", "Сапоги", "Ботинки", "Балетки", "Тапочки", "Зимняя обувь"),
            "Аксессуары" to listOf("Аксессуары"),
            "Пляжная одежда" to listOf("Купальник", "Пляжная одежда", "Шлепанцы")
        )

        groups.forEach { (groupName, categories) ->
            val groupId = CategoryGroupTable.insert {
                it[name] = groupName
            } get CategoryGroupTable.id

            categories.forEach { categoryName ->
                CategoryTable.insert {
                    it[this.groupId] = groupId
                    it[name] = categoryName
                }
            }
        }
    }

    private fun seedSeasons() {
        if (SeasonTable.selectAll().count() > 0) return
        listOf("Лето", "Осень", "Зима", "Весна", "Круглый год").forEach { seasonName ->
            SeasonTable.insert { it[name] = seasonName }
        }
    }

    private fun seedColors() {
        if (ColorTable.selectAll().count() > 0) return
        listOf(
            "Черный", "Серый", "Белый", "Бежевый", "Красный", "Розовый",
            "Оранжевый", "Желтый", "Зеленый", "Бирюзовый", "Голубой", "Синий",
            "Фиолетовый", "Фуксия", "Бордовый", "Коричневый", "Металлик",
            "Золотой", "Хаки", "Разноцветный", "Прозрачный"
        ).forEach { colorName ->
            ColorTable.insert { it[name] = colorName }
        }
    }

    private fun seedMaterials() {
        if (MaterialTable.selectAll().count() > 0) return
        listOf(
            "Хлопок", "Вискоза", "Лайкра", "Лён", "Акрил", "Полиэстер",
            "Искусственная кожа", "Натуральная кожа", "Кашемир", "Бархат",
            "Ангора", "Люрекс", "Замша", "Шелк", "Шерсть", "Мех", "Деним",
            "Муслин", "Нейлон", "Бамбук", "Шифон", "Флис"
        ).forEach { materialName ->
            MaterialTable.insert { it[name] = materialName }
        }
    }

    private fun seedStyles() {
        if (StyleTable.selectAll().count() > 0) return
        listOf(
            "Повседневный", "Спортивный", "Уютный", "Домашний", "Элегантный",
            "Нарядный", "Классический", "Минималистичный", "Деловой", "Кежуал",
            "Романтичный", "Бохо", "Пляжный", "Смарт-кэжуал", "Строгий",
            "Восточный", "Клубный"
        ).forEach { styleName ->
            StyleTable.insert { it[name] = styleName }
        }
    }

    private fun seedTripTypes() {
        if (TripTypeTable.selectAll().count() > 0) return
        listOf(
            "Активный отдых", "Отдых у воды", "Городское путешествие",
            "Поездки на машине", "Рабочая командировка", "Экспедиция"
        ).forEach { typeName ->
            TripTypeTable.insert { it[name] = typeName }
        }
    }

    private fun seedClimates() {
        if (ClimateTable.selectAll().count() > 0) return
        listOf(
            "Теплый и влажный", "Теплый и сухой", "Умеренный", "Холодный"
        ).forEach { climateName ->
            ClimateTable.insert { it[name] = climateName }
        }
    }

    private fun seedActivities() {
        if (ActivityTable.selectAll().count() > 0) return
        listOf(
            "Прогулки", "Пляж", "Спорт", "Хайкинг", "Еда",
            "Культурные мероприятия", "Транспорт", "Рабочие встречи"
        ).forEach { activityName ->
            ActivityTable.insert { it[name] = activityName }
        }
    }

    private fun seedLuggageTypes() {
        if (LuggageTypeTable.selectAll().count() > 0) return
        listOf(
            "Рюкзак", "Ручная кладь", "Чемодан", "Большой чемодан"
        ).forEach { luggageName ->
            LuggageTypeTable.insert { it[name] = luggageName }
        }
    }

    private fun seedLabels() {
        if (LabelTable.selectAll().count() > 0) return
        listOf(
            "Винтажная", "С поясом", "С карманами", "С капюшоном", "С принтом",
            "С вышивкой", "Теплая", "Элегантная", "Спортивная", "Оверсайз",
            "С длинным рукавом", "Приталенная", "Базовая", "С открытыми плечами",
            "Длинная", "Обтягивающая", "С асимметрией", "На пуговицах",
            "На молнии", "С разрезом"
        ).forEach { labelName ->
            LabelTable.insert {
                it[name] = labelName
                it[userId] = null
            }
        }
    }
}