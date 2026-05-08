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
import com.wardrobeapp.server.data.db.tables.TemplateItemTable
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
            seedTemplateItems()
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

    private fun seedTemplateItems() {
        if (TemplateItemTable.selectAll().count() > 0) return

        val items = listOf(
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/топ_1_j5zdku.png", "Топ", Triple("Лето", "Белый", "Хлопок")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/рубашка_голубая_sojmdq.png", "Рубашка", Triple("Круглый год", "Голубой", "Хлопок")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/джинсы_белые_eli29b.png", "Джинсы", Triple("Лето", "Бежевый", "Деним")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/джинсы__vjkmsf.png", "Джинсы", Triple("Лето", "Голубой", "Деним")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/джинсовка_1_ef0pde.png", "Куртка", Triple("Лето", "Голубой", "Деним")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/водолазка_1_psf8ht.png", "Лонгслив", Triple("Зима", "Белый", "Вискоза")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/шорты_1_nac6yt.png", "Шорты", Triple("Лето", "Голубой", "Деним")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/пальто_1_d5jhle.png", "Пальто", Triple("Осень", "Серый", "Шерсть")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/уги_1_ysam5m.png", "Ботинки", Triple("Осень", "Оранжевый", "Шерсть")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/поло_1_b9ihxd.png", "Поло", Triple("Круглый год", "Синий", "Вискоза")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/худи_3_creua6.png", "Худи", Triple("Круглый год", "Серый", "Полиэстер")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/кардиган_1_kdupgc.png", "Кардиган", Triple("Зима", "Серый", "Хлопок")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/худи_2_g8kv0k.png", "Худи", Triple("Круглый год", "Голубой", "Хлопок")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/брюки_1_wvn3ll.png", "Брюки", Triple("Круглый год", "Черный", "Хлопок")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/кардиган_2_aydpl7.png", "Кардиган", Triple("Зима", "Белый", "Шерсть")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/кроссовки_2_owsb84.png", "Кроссовки", Triple("Лето", "Металлик", "Полиэстер")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/кроссовки_3_ukh7mb.png", "Кроссовки", Triple("Лето", "Черный", "Хлопок")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/свитер_1_r4qti1.png", "Свитер", Triple("Зима", "Бежевый", "Шерсть")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/топ_2_nl3pgw.png", "Топ", Triple("Круглый год", "Черный", "Хлопок")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/кроссовки_1_mkylwm.png", "Кроссовки", Triple("Лето", "Белый", "Искусственная кожа")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/рубашка_белая_nm2uno.png", "Рубашка", Triple("Круглый год", "Белый", "Хлопок")),
            Triple("https://res.cloudinary.com/djrxlxqln/image/upload/худи_1_yvkjm5.png", "Худи", Triple("Круглый год", "Белый", "Хлопок"))
        )

        items.forEach { (imageUrl, categoryName, props) ->
            val (seasonName, colorName, materialName) = props

            val categoryId = CategoryTable.selectAll()
                .where { CategoryTable.name eq categoryName }
                .single()[CategoryTable.id]

            val seasonId = SeasonTable.selectAll()
                .where { SeasonTable.name eq seasonName }
                .single()[SeasonTable.id]

            val colorId = ColorTable.selectAll()
                .where { ColorTable.name eq colorName }
                .single()[ColorTable.id]

            val materialId = MaterialTable.selectAll()
                .where { MaterialTable.name eq materialName }
                .single()[MaterialTable.id]

            TemplateItemTable.insert {
                it[TemplateItemTable.imageUrl] = imageUrl
                it[TemplateItemTable.categoryId] = categoryId
                it[TemplateItemTable.seasonId] = seasonId
                it[TemplateItemTable.colorId] = colorId
                it[TemplateItemTable.materialId] = materialId
            }
        }
    }
}