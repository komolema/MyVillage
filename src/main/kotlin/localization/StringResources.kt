package localization

interface StringResources {
    // Common strings
    val save: String
    val cancel: String
    val edit: String
    val delete: String
    val back: String
    val changesSaved: String

    // Navigation
    val next: String
    val previous: String
    val contentDescCancel: String
    val contentDescSave: String
    val contentDescPrevious: String
    val contentDescNext: String

    // Settings strings
    val settings: String
    val language: String
    val selectLanguage: String
    val appearance: String
    val darkMode: String

    // Resident strings
    val resident: String
    val residents: String
    val addResident: String
    val editResident: String
    val firstName: String
    val lastName: String
    val dateOfBirth: String
    val gender: String
    val idNumber: String

    // Other common sections
    val qualifications: String
    val dependents: String
    val residence: String
    val employment: String

    // Dependent strings
    val dependentsInformation: String
    val addDependent: String
    val editDependent: String
    val name: String
    val surname: String
    val genderMale: String
    val genderFemale: String
    val genderOther: String
    val toggleDropdown: String

    // Residence strings
    val residenceInformation: String
    val street: String
    val houseNumber: String
    val suburb: String
    val town: String
    val postalCode: String
    val geoCoordinates: String
    val landmark: String
    val occupationDate: String
    val noResidenceInfo: String
    val editResidence: String
    val optional: String
}

class EnglishStringResources : StringResources {
    override val dependentsInformation = "Dependents Information"
    override val addDependent = "Add Dependent"
    override val editDependent = "Edit Dependent"
    override val name = "Name"
    override val surname = "Surname"
    override val genderMale = "Male"
    override val genderFemale = "Female"
    override val genderOther = "Other"
    override val toggleDropdown = "Toggle dropdown"
    override val save = "Save"
    override val cancel = "Cancel"
    override val edit = "Edit"
    override val delete = "Delete"
    override val back = "Back"
    override val changesSaved = "Changes saved successfully"

    override val next = "Next"
    override val previous = "Previous"
    override val contentDescCancel = "Cancel"
    override val contentDescSave = "Save"
    override val contentDescPrevious = "Previous"
    override val contentDescNext = "Next"

    override val settings = "Settings"
    override val language = "Language"
    override val selectLanguage = "Select Language"
    override val appearance = "Appearance"
    override val darkMode = "Dark Mode"

    override val resident = "Resident"
    override val residents = "Residents"
    override val addResident = "Add Resident"
    override val editResident = "Edit Resident"
    override val firstName = "First Name"
    override val lastName = "Last Name"
    override val dateOfBirth = "Date of Birth"
    override val gender = "Gender"
    override val idNumber = "ID Number"

    override val qualifications = "Qualifications"
    override val dependents = "Dependents"
    override val residence = "Residence"
    override val employment = "Employment"

    override val residenceInformation = "Residence Information"
    override val street = "Street"
    override val houseNumber = "House Number"
    override val suburb = "Suburb"
    override val town = "Town"
    override val postalCode = "Postal Code"
    override val geoCoordinates = "Geo Coordinates"
    override val landmark = "Landmark"
    override val occupationDate = "Occupation Date"
    override val noResidenceInfo = "No residence information available"
    override val editResidence = "Edit Residence"
    override val optional = "Optional"
}

class SetswanaStringResources : StringResources {
    override val dependentsInformation = "Tshedimosetso ya Bana"
    override val addDependent = "Oketsa Ngwana"
    override val editDependent = "Fetola Ngwana"
    override val name = "Leina"
    override val surname = "Sefane"
    override val genderMale = "Monna"
    override val genderFemale = "Mosadi"
    override val genderOther = "Tse dingwe"
    override val toggleDropdown = "Bula/Tswala tlhopho"
    override val save = "Boloka"
    override val cancel = "Khansela"
    override val edit = "Fetola"
    override val delete = "Tlosa"
    override val back = "Boela morago"
    override val changesSaved = "Diphetogo di bolokilwe ka katlego"

    override val next = "E e latelang"
    override val previous = "E e fetileng"
    override val contentDescCancel = "Khansela"
    override val contentDescSave = "Boloka"
    override val contentDescPrevious = "E e fetileng"
    override val contentDescNext = "E e latelang"

    override val settings = "Dipeakanyo"
    override val language = "Puo"
    override val selectLanguage = "Tlhopha Puo"
    override val appearance = "Tebego"
    override val darkMode = "Mokgwa wa Lefifi"

    override val resident = "Monni"
    override val residents = "Baagi"
    override val addResident = "Oketsa Monni"
    override val editResident = "Fetola Monni"
    override val firstName = "Leina la Ntlha"
    override val lastName = "Sefane"
    override val dateOfBirth = "Letsatsi la Matsalo"
    override val gender = "Bong"
    override val idNumber = "Nomoro ya ID"

    override val qualifications = "Dithuto"
    override val dependents = "Bana"
    override val residence = "Bonno"
    override val employment = "Tiro"

    override val residenceInformation = "Tshedimosetso ya Bonno"
    override val street = "Mmila"
    override val houseNumber = "Nomoro ya Ntlo"
    override val suburb = "Toropo-potlana"
    override val town = "Toropo"
    override val postalCode = "Nomoro ya Poso"
    override val geoCoordinates = "Dikaelo tsa Lefatshe"
    override val landmark = "Letshwao la Lefelo"
    override val occupationDate = "Letsatsi la go Tsena"
    override val noResidenceInfo = "Ga gona tshedimosetso ya bonno"
    override val editResidence = "Fetola Bonno"
    override val optional = "Ga e pateletsege"
}

class XhosaStringResources : StringResources {
    override val dependentsInformation = "Ulwazi lwabaxhomekeki"
    override val addDependent = "Yongeza umxhomekeki"
    override val editDependent = "Hlela umxhomekeki"
    override val name = "Igama"
    override val surname = "Ifani"
    override val genderMale = "Indoda"
    override val genderFemale = "Umfazi"
    override val genderOther = "Okunye"
    override val toggleDropdown = "Vula/Vala ukhetho"
    override val save = "Gcina"
    override val cancel = "Rhoxisa"
    override val edit = "Hlela"
    override val delete = "Cima"
    override val back = "Buyela emva"
    override val changesSaved = "Utshintsho lugcinwe ngempumelelo"

    override val next = "Okulandelayo"
    override val previous = "Okungaphambili"
    override val contentDescCancel = "Rhoxisa"
    override val contentDescSave = "Gcina"
    override val contentDescPrevious = "Okungaphambili"
    override val contentDescNext = "Okulandelayo"

    override val settings = "Iisethingi"
    override val language = "Ulwimi"
    override val selectLanguage = "Khetha Ulwimi"
    override val appearance = "Inkangeleko"
    override val darkMode = "Umoya omnyama"

    override val resident = "Umhlali"
    override val residents = "Abahlali"
    override val addResident = "Yongeza Umhlali"
    override val editResident = "Hlela Umhlali"
    override val firstName = "Igama"
    override val lastName = "Ifani"
    override val dateOfBirth = "Umhla wokuzalwa"
    override val gender = "Isini"
    override val idNumber = "Inombolo yesazisi"

    override val qualifications = "Iziqinisekiso"
    override val dependents = "Abaxhomekeki"
    override val residence = "Indawo yokuhlala"
    override val employment = "Ingqesho"

    override val residenceInformation = "Ulwazi lwendawo yokuhlala"
    override val street = "Isitalato"
    override val houseNumber = "Inombolo yendlu"
    override val suburb = "Ummandla"
    override val town = "Idolophu"
    override val postalCode = "Ikhowudi yeposi"
    override val geoCoordinates = "Iindawo zomhlaba"
    override val landmark = "Uphawu"
    override val occupationDate = "Umhla wokuhlala"
    override val noResidenceInfo = "Akukho lwazi lwendawo yokuhlala"
    override val editResidence = "Hlela indawo yokuhlala"
    override val optional = "Ayinyanzelekanga"
}

class SepediStringResources : StringResources {
    override val dependentsInformation = "Tshedimošo ya Bana"
    override val addDependent = "Oketša Ngwana"
    override val editDependent = "Fetola Ngwana"
    override val name = "Leina"
    override val surname = "Sefane"
    override val genderMale = "Monna"
    override val genderFemale = "Mosadi"
    override val genderOther = "Tše dingwe"
    override val toggleDropdown = "Bula/Tswalela kgetho"
    override val save = "Boloka"
    override val cancel = "Khansela"
    override val edit = "Fetola"
    override val delete = "Phumula"
    override val back = "Morago"
    override val changesSaved = "Diphetogo di bolokilwe ka katlego"

    override val next = "Ye e latelago"
    override val previous = "Ya go feta"
    override val contentDescCancel = "Khansela"
    override val contentDescSave = "Boloka"
    override val contentDescPrevious = "Ya go feta"
    override val contentDescNext = "Ye e latelago"

    override val settings = "Dipeakanyo"
    override val language = "Polelo"
    override val selectLanguage = "Kgetha Polelo"
    override val appearance = "Ponagalo"
    override val darkMode = "Mokgwa wa Leswiswi"

    override val resident = "Modudi"
    override val residents = "Badudi"
    override val addResident = "Oketša Modudi"
    override val editResident = "Fetola Modudi"
    override val firstName = "Leina la Mathomo"
    override val lastName = "Sefane"
    override val dateOfBirth = "Letšatšikgwedi la Matswalo"
    override val gender = "Bong"
    override val idNumber = "Nomoro ya ID"

    override val qualifications = "Dithuto"
    override val dependents = "Bana"
    override val residence = "Bodulo"
    override val employment = "Mošomo"

    override val residenceInformation = "Tshedimošo ya Bodulo"
    override val street = "Mmila"
    override val houseNumber = "Nomoro ya Ntlo"
    override val suburb = "Toropo-potlana"
    override val town = "Toropo"
    override val postalCode = "Nomoro ya Poso"
    override val geoCoordinates = "Maemo a Lefase"
    override val landmark = "Leswao la Lefelo"
    override val occupationDate = "Letšatši la go Dula"
    override val noResidenceInfo = "Ga go na tshedimošo ya bodulo"
    override val editResidence = "Fetola Bodulo"
    override val optional = "Ga e gapeletšwe"
}

object StringResourcesManager {
    fun getStringResources(locale: SupportedLanguage): StringResources {
        return when (locale) {
            SupportedLanguage.ENGLISH -> EnglishStringResources()
            SupportedLanguage.SETSWANA -> SetswanaStringResources()
            SupportedLanguage.XHOSA -> XhosaStringResources()
            SupportedLanguage.SEPEDI -> SepediStringResources()
        }
    }

    fun getCurrentStringResources(): StringResources {
        return getStringResources(LocaleManager.getCurrentLocale())
    }
}
