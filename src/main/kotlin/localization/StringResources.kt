package localization

interface StringResources {
    // Common strings
    val save: String
    val cancel: String
    val edit: String
    val delete: String
    val back: String
    val changesSaved: String
    val add: String
    val reload: String
    val search: String
    val page: String
    val of: String
    val showing: String
    val noAddress: String
    val selectGender: String
    val age: String
    val glossary: String
    val searchPlaceholder: String
    val addNewResident: String

    // Form field labels
    val firstNameLabel: String
    val lastNameLabel: String
    val dateOfBirthLabel: String
    val genderLabel: String
    val idNumberLabel: String
    val phoneNumberLabel: String
    val emailLabel: String

    // Form placeholders
    val enterFirstName: String
    val enterLastName: String
    val selectDateOfBirth: String
    val selectGenderPlaceholder: String
    val enterIdNumber: String
    val enterPhoneNumber: String
    val enterEmail: String

    // Validation errors
    val invalidEmailFormat: String
    val invalidPhoneFormat: String
    val idNumberMustBe13Digits: String

    // Date picker
    val today: String
    val month: String
    val year: String
    val selectDate: String

    // Gender dropdown
    val closeGenderSelection: String
    val openGenderSelection: String
    val genderMaleDisplay: String
    val genderFemaleDisplay: String

    // Residence
    val proofOfAddress: String
    val addResidence: String

    // Navigation
    val next: String
    val previous: String
    val contentDescCancel: String
    val contentDescSave: String
    val contentDescPrevious: String
    val contentDescNext: String
    val contentDescAdd: String
    val contentDescEdit: String
    val contentDescDelete: String
    val contentDescSaveChanges: String
    val contentDescReload: String
    val contentDescGlossary: String

    // Settings strings
    val settings: String
    val language: String
    val selectLanguage: String
    val appearance: String
    val darkMode: String

    // Dashboard strings
    val animal: String
    val resource: String
    val admin: String
    val adminScreen: String
    val generatedDocuments: String
    val viewGeneratedDocuments: String
    val generatedDocumentsList: String

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
    override val animal = "Animal"
    override val resource = "Resource"
    override val admin = "Admin"
    override val adminScreen = "Admin Screen"
    override val generatedDocuments = "Generated Documents"
    override val viewGeneratedDocuments = "View Generated Documents"
    override val generatedDocumentsList = "Generated Documents List"

    // Form field labels
    override val firstNameLabel = "First Name:"
    override val lastNameLabel = "Last Name:"
    override val dateOfBirthLabel = "Date of Birth:"
    override val genderLabel = "Gender:"
    override val idNumberLabel = "ID Number:"
    override val phoneNumberLabel = "Phone Number:"
    override val emailLabel = "Email:"

    // Form placeholders
    override val enterFirstName = "Enter first name"
    override val enterLastName = "Enter last name"
    override val selectDateOfBirth = "Select date of birth"
    override val selectGenderPlaceholder = "Select gender"
    override val enterIdNumber = "Enter 13-digit ID number"
    override val enterPhoneNumber = "Enter phone number (e.g., +27123456789)"
    override val enterEmail = "Enter email address"

    // Validation errors
    override val invalidEmailFormat = "Invalid email format"
    override val invalidPhoneFormat = "Invalid phone number format"
    override val idNumberMustBe13Digits = "ID number must be 13 digits"

    // Date picker
    override val today = "Today"
    override val month = "Month"
    override val year = "Year"
    override val selectDate = "Select date"

    // Gender dropdown
    override val closeGenderSelection = "Close gender selection"
    override val openGenderSelection = "Open gender selection"
    override val genderMaleDisplay = "Male"
    override val genderFemaleDisplay = "Female"

    // Residence
    override val proofOfAddress = "Proof of Address"
    override val addResidence = "Add Residence"

    // Common strings
    override val add = "Add"
    override val reload = "Reload"
    override val search = "Search"
    override val page = "Page"
    override val of = "of"
    override val showing = "Showing"
    override val noAddress = "No address"
    override val selectGender = "Select Gender"
    override val age = "Age"
    override val glossary = "Glossary"
    override val searchPlaceholder = "Search by ID number, name or other details..."
    override val addNewResident = "Add New Resident"

    // Content descriptions
    override val contentDescAdd = "Add"
    override val contentDescEdit = "Edit"
    override val contentDescDelete = "Delete"
    override val contentDescSaveChanges = "Save Changes"
    override val contentDescReload = "Reload"
    override val contentDescGlossary = "Glossary"

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
    override val animal = "Phologolo"
    override val resource = "Motswedi"
    override val admin = "Motsamaisi"
    override val adminScreen = "Skrine ya Motsamaisi"
    override val generatedDocuments = "Dikwalo tse di Tlhagisitsweng"
    override val viewGeneratedDocuments = "Lebelela Dikwalo tse di Tlhagisitsweng"
    override val generatedDocumentsList = "Lenane la Dikwalo tse di Tlhagisitsweng"

    // Form field labels
    override val firstNameLabel = "Leina la Ntlha:"
    override val lastNameLabel = "Sefane:"
    override val dateOfBirthLabel = "Letsatsi la Matsalo:"
    override val genderLabel = "Bong:"
    override val idNumberLabel = "Nomoro ya ID:"
    override val phoneNumberLabel = "Nomoro ya Mogala:"
    override val emailLabel = "Imeile:"

    // Form placeholders
    override val enterFirstName = "Tsenya leina la ntlha"
    override val enterLastName = "Tsenya sefane"
    override val selectDateOfBirth = "Tlhopha letsatsi la matsalo"
    override val selectGenderPlaceholder = "Tlhopha bong"
    override val enterIdNumber = "Tsenya nomoro ya ID ya dinomoro tse 13"
    override val enterPhoneNumber = "Tsenya nomoro ya mogala (sk., +27123456789)"
    override val enterEmail = "Tsenya aterese ya imeile"

    // Validation errors
    override val invalidEmailFormat = "Sebopego sa imeile se sa siamang"
    override val invalidPhoneFormat = "Sebopego sa nomoro ya mogala se sa siamang"
    override val idNumberMustBe13Digits = "Nomoro ya ID e tshwanetse go nna dinomoro tse 13"

    // Date picker
    override val today = "Gompieno"
    override val month = "Kgwedi"
    override val year = "Ngwaga"
    override val selectDate = "Tlhopha letlha"

    // Gender dropdown
    override val closeGenderSelection = "Tswala tlhopho ya bong"
    override val openGenderSelection = "Bula tlhopho ya bong"
    override val genderMaleDisplay = "Monna"
    override val genderFemaleDisplay = "Mosadi"

    // Residence
    override val proofOfAddress = "Bopaki jwa Aterese"
    override val addResidence = "Oketsa Bonno"

    // Common strings
    override val add = "Oketsa"
    override val reload = "Laisa sešwa"
    override val search = "Batla"
    override val page = "Tsebe"
    override val of = "ya"
    override val showing = "Go bontsha"
    override val noAddress = "Ga go na aterese"
    override val selectGender = "Tlhopha Bong"
    override val age = "Dingwaga"
    override val glossary = "Thanodi"
    override val searchPlaceholder = "Batla ka nomoro ya ID, leina kgotsa dintlha tse dingwe..."
    override val addNewResident = "Oketsa Monni yo Mošwa"

    // Content descriptions
    override val contentDescAdd = "Oketsa"
    override val contentDescEdit = "Fetola"
    override val contentDescDelete = "Tlosa"
    override val contentDescSaveChanges = "Boloka Diphetogo"
    override val contentDescReload = "Laisa sešwa"
    override val contentDescGlossary = "Thanodi"

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
    override val animal = "Isilwanyana"
    override val resource = "Izixhobo"
    override val admin = "Umlawuli"
    override val adminScreen = "Iskrini Somlawuli"
    override val generatedDocuments = "Amaxwebhu Akhiqiziweyo"
    override val viewGeneratedDocuments = "Jonga Amaxwebhu Akhiqiziweyo"
    override val generatedDocumentsList = "Uluhlu Lwamaxwebhu Akhiqiziweyo"

    // Form field labels
    override val firstNameLabel = "Igama:"
    override val lastNameLabel = "Ifani:"
    override val dateOfBirthLabel = "Umhla wokuzalwa:"
    override val genderLabel = "Isini:"
    override val idNumberLabel = "Inombolo yesazisi:"
    override val phoneNumberLabel = "Inombolo yefowuni:"
    override val emailLabel = "I-imeyile:"

    // Form placeholders
    override val enterFirstName = "Faka igama"
    override val enterLastName = "Faka ifani"
    override val selectDateOfBirth = "Khetha umhla wokuzalwa"
    override val selectGenderPlaceholder = "Khetha isini"
    override val enterIdNumber = "Faka inombolo yesazisi enezinombolo ezili-13"
    override val enterPhoneNumber = "Faka inombolo yefowuni (umz., +27123456789)"
    override val enterEmail = "Faka idilesi ye-imeyile"

    // Validation errors
    override val invalidEmailFormat = "Ifomathi ye-imeyile engasebenziyo"
    override val invalidPhoneFormat = "Ifomathi yenombolo yefowuni engasebenziyo"
    override val idNumberMustBe13Digits = "Inombolo yesazisi kufuneka ibe nezinombolo ezili-13"

    // Date picker
    override val today = "Namhlanje"
    override val month = "Inyanga"
    override val year = "Unyaka"
    override val selectDate = "Khetha umhla"

    // Gender dropdown
    override val closeGenderSelection = "Vala ukukhetha isini"
    override val openGenderSelection = "Vula ukukhetha isini"
    override val genderMaleDisplay = "Indoda"
    override val genderFemaleDisplay = "Umfazi"

    // Residence
    override val proofOfAddress = "Ubungqina bedilesi"
    override val addResidence = "Yongeza Indawo yokuhlala"

    // Common strings
    override val add = "Yongeza"
    override val reload = "Layisha kwakhona"
    override val search = "Khangela"
    override val page = "Iphepha"
    override val of = "ye"
    override val showing = "Ibonisa"
    override val noAddress = "Akukho dilesi"
    override val selectGender = "Khetha Isini"
    override val age = "Iminyaka"
    override val glossary = "Isichazi-magama"
    override val searchPlaceholder = "Khangela ngenombolo ye-ID, igama okanye ezinye iinkcukacha..."
    override val addNewResident = "Yongeza Umhlali Omtsha"

    // Content descriptions
    override val contentDescAdd = "Yongeza"
    override val contentDescEdit = "Hlela"
    override val contentDescDelete = "Cima"
    override val contentDescSaveChanges = "Gcina Utshintsho"
    override val contentDescReload = "Layisha kwakhona"
    override val contentDescGlossary = "Isichazi-magama"

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
    override val animal = "Phoofolo"
    override val resource = "Mothopo"
    override val admin = "Molaodi"
    override val adminScreen = "Skrini ya Molaodi"
    override val generatedDocuments = "Ditokumente tše di Hlamilwego"
    override val viewGeneratedDocuments = "Lebelela Ditokumente tše di Hlamilwego"
    override val generatedDocumentsList = "Lenaneo la Ditokumente tše di Hlamilwego"

    // Form field labels
    override val firstNameLabel = "Leina la Mathomo:"
    override val lastNameLabel = "Sefane:"
    override val dateOfBirthLabel = "Letšatšikgwedi la Matswalo:"
    override val genderLabel = "Bong:"
    override val idNumberLabel = "Nomoro ya ID:"
    override val phoneNumberLabel = "Nomoro ya Mogala:"
    override val emailLabel = "Imeile:"

    // Form placeholders
    override val enterFirstName = "Tsenya leina la mathomo"
    override val enterLastName = "Tsenya sefane"
    override val selectDateOfBirth = "Kgetha letšatšikgwedi la matswalo"
    override val selectGenderPlaceholder = "Kgetha bong"
    override val enterIdNumber = "Tsenya nomoro ya ID ya dinomoro tše 13"
    override val enterPhoneNumber = "Tsenya nomoro ya mogala (mohlala, +27123456789)"
    override val enterEmail = "Tsenya aterese ya imeile"

    // Validation errors
    override val invalidEmailFormat = "Sebopego sa imeile se sa nepagalang"
    override val invalidPhoneFormat = "Sebopego sa nomoro ya mogala se sa nepagalang"
    override val idNumberMustBe13Digits = "Nomoro ya ID e swanetše go ba le dinomoro tše 13"

    // Date picker
    override val today = "Lehono"
    override val month = "Kgwedi"
    override val year = "Ngwaga"
    override val selectDate = "Kgetha letšatšikgwedi"

    // Gender dropdown
    override val closeGenderSelection = "Tswalela kgetho ya bong"
    override val openGenderSelection = "Bula kgetho ya bong"
    override val genderMaleDisplay = "Monna"
    override val genderFemaleDisplay = "Mosadi"

    // Residence
    override val proofOfAddress = "Bohlatse bja Aterese"
    override val addResidence = "Oketša Bodulo"

    // Common strings
    override val add = "Oketša"
    override val reload = "Laela gape"
    override val search = "Nyaka"
    override val page = "Letlakala"
    override val of = "la"
    override val showing = "Go bontšha"
    override val noAddress = "Ga go na aterese"
    override val selectGender = "Kgetha Bong"
    override val age = "Mengwaga"
    override val glossary = "Pukuntšu"
    override val searchPlaceholder = "Nyaka ka nomoro ya ID, leina goba dintlha tše dingwe..."
    override val addNewResident = "Oketša Modudi yo Mofsa"

    // Content descriptions
    override val contentDescAdd = "Oketša"
    override val contentDescEdit = "Fetola"
    override val contentDescDelete = "Phumula"
    override val contentDescSaveChanges = "Boloka Diphetogo"
    override val contentDescReload = "Laela gape"
    override val contentDescGlossary = "Pukuntšu"

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

class ShonaStringResources : StringResources {
    override val animal = "Mhuka"
    override val resource = "Zvekushandisa"
    override val admin = "Mutongi"
    override val adminScreen = "Chiongororo cheMutongi"
    override val generatedDocuments = "Magwaro Akagadzirwa"
    override val viewGeneratedDocuments = "Ona Magwaro Akagadzirwa"
    override val generatedDocumentsList = "Rondedzero yeMagwaro Akagadzirwa"

    // Form field labels
    override val firstNameLabel = "Zita Rokutanga:"
    override val lastNameLabel = "Zita Rokupedzisira:"
    override val dateOfBirthLabel = "Zuva Rokuberekwa:"
    override val genderLabel = "Mhando:"
    override val idNumberLabel = "Nhamba yeID:"
    override val phoneNumberLabel = "Nhamba yeFoni:"
    override val emailLabel = "Imeyili:"

    // Form placeholders
    override val enterFirstName = "Isa zita rokutanga"
    override val enterLastName = "Isa zita rokupedzisira"
    override val selectDateOfBirth = "Sarudza zuva rokuberekwa"
    override val selectGenderPlaceholder = "Sarudza mhando"
    override val enterIdNumber = "Isa nhamba yeID ine nhamba 13"
    override val enterPhoneNumber = "Isa nhamba yefoni (mufananidzo, +27123456789)"
    override val enterEmail = "Isa kero yeimeyili"

    // Validation errors
    override val invalidEmailFormat = "Fomati yeimeyili isina kukodzera"
    override val invalidPhoneFormat = "Fomati yenhamba yefoni isina kukodzera"
    override val idNumberMustBe13Digits = "Nhamba yeID inofanira kuva nenhamba 13"

    // Date picker
    override val today = "Nhasi"
    override val month = "Mwedzi"
    override val year = "Gore"
    override val selectDate = "Sarudza zuva"

    // Gender dropdown
    override val closeGenderSelection = "Vhara kusarudza mhando"
    override val openGenderSelection = "Vhura kusarudza mhando"
    override val genderMaleDisplay = "Murume"
    override val genderFemaleDisplay = "Mukadzi"

    // Residence
    override val proofOfAddress = "Umbowo weKero"
    override val addResidence = "Wedzera Nzvimbo Yekugara"

    // Common strings
    override val add = "Wedzera"
    override val reload = "Rodha zvakare"
    override val search = "Tsvaga"
    override val page = "Peji"
    override val of = "ye"
    override val showing = "Kuratidza"
    override val noAddress = "Hapana kero"
    override val selectGender = "Sarudza Mhando"
    override val age = "Makore"
    override val glossary = "Duramanzwi"
    override val searchPlaceholder = "Tsvaga nenombero yeID, zita kana mamwe mashoko..."
    override val addNewResident = "Wedzera Mugariri Mutsva"

    // Content descriptions
    override val contentDescAdd = "Wedzera"
    override val contentDescEdit = "Gadzirisa"
    override val contentDescDelete = "Dzima"
    override val contentDescSaveChanges = "Chengetedza Shanduko"
    override val contentDescReload = "Rodha zvakare"
    override val contentDescGlossary = "Duramanzwi"

    override val dependentsInformation = "Ruzivo rwevachengeti"
    override val addDependent = "Wedzera Muchengeti"
    override val editDependent = "Gadzirisa Muchengeti"
    override val name = "Zita"
    override val surname = "Surname"
    override val genderMale = "Murume"
    override val genderFemale = "Mukadzi"
    override val genderOther = "Zvimwe"
    override val toggleDropdown = "Toggle dropdown"
    override val save = "Chengetedza"
    override val cancel = "Kanzura"
    override val edit = "Gadzirisa"
    override val delete = "Dzima"
    override val back = "Dzokera"
    override val changesSaved = "Shanduko dzachengetedzwa zvakanaka"

    override val next = "Tevere"
    override val previous = "Yapfuura"
    override val contentDescCancel = "Kanzura"
    override val contentDescSave = "Chengetedza"
    override val contentDescPrevious = "Yapfuura"
    override val contentDescNext = "Tevere"

    override val settings = "Zvimiso"
    override val language = "Mutauro"
    override val selectLanguage = "Sarudza Mutauro"
    override val appearance = "Kuonekwa"
    override val darkMode = "Mwenje Wakadzima"

    override val resident = "Mugariri"
    override val residents = "Vagari"
    override val addResident = "Wedzera Mugariri"
    override val editResident = "Gadzirisa Mugariri"
    override val firstName = "Zita Rokutanga"
    override val lastName = "Zita Rokupedzisira"
    override val dateOfBirth = "Zuva Rokuberekwa"
    override val gender = "Mhando"
    override val idNumber = "Nhamba yeID"

    override val qualifications = "Zvikwanisiro"
    override val dependents = "Vachengeti"
    override val residence = "Nzvimbo Yekugara"
    override val employment = "Basa"

    override val residenceInformation = "Ruzivo Rwenzvimbo Yekugara"
    override val street = "Mugwagwa"
    override val houseNumber = "Nhamba yeImba"
    override val suburb = "Nzvimbo"
    override val town = "Guta"
    override val postalCode = "Nhamba yePosi"
    override val geoCoordinates = "Nzvimbo Pasi"
    override val landmark = "Chiratidzo"
    override val occupationDate = "Zuva Rokutanga Kugara"
    override val noResidenceInfo = "Hapana ruzivo rwenzvimbo yekugara"
    override val editResidence = "Gadzirisa Nzvimbo Yekugara"
    override val optional = "Hazvina Kukosha"
}

class SwahiliStringResources : StringResources {
    override val animal = "Mnyama"
    override val resource = "Rasilimali"
    override val admin = "Msimamizi"
    override val adminScreen = "Skrini ya Msimamizi"
    override val generatedDocuments = "Nyaraka Zilizozalishwa"
    override val viewGeneratedDocuments = "Tazama Nyaraka Zilizozalishwa"
    override val generatedDocumentsList = "Orodha ya Nyaraka Zilizozalishwa"

    // Form field labels
    override val firstNameLabel = "Jina la Kwanza:"
    override val lastNameLabel = "Jina la Mwisho:"
    override val dateOfBirthLabel = "Tarehe ya Kuzaliwa:"
    override val genderLabel = "Jinsia:"
    override val idNumberLabel = "Nambari ya Kitambulisho:"
    override val phoneNumberLabel = "Nambari ya Simu:"
    override val emailLabel = "Barua pepe:"

    // Form placeholders
    override val enterFirstName = "Ingiza jina la kwanza"
    override val enterLastName = "Ingiza jina la mwisho"
    override val selectDateOfBirth = "Chagua tarehe ya kuzaliwa"
    override val selectGenderPlaceholder = "Chagua jinsia"
    override val enterIdNumber = "Ingiza nambari ya kitambulisho yenye tarakimu 13"
    override val enterPhoneNumber = "Ingiza nambari ya simu (mfano, +27123456789)"
    override val enterEmail = "Ingiza anwani ya barua pepe"

    // Validation errors
    override val invalidEmailFormat = "Muundo batili wa barua pepe"
    override val invalidPhoneFormat = "Muundo batili wa nambari ya simu"
    override val idNumberMustBe13Digits = "Nambari ya kitambulisho lazima iwe na tarakimu 13"

    // Date picker
    override val today = "Leo"
    override val month = "Mwezi"
    override val year = "Mwaka"
    override val selectDate = "Chagua tarehe"

    // Gender dropdown
    override val closeGenderSelection = "Funga uchaguzi wa jinsia"
    override val openGenderSelection = "Fungua uchaguzi wa jinsia"
    override val genderMaleDisplay = "Mwanaume"
    override val genderFemaleDisplay = "Mwanamke"

    // Residence
    override val proofOfAddress = "Uthibitisho wa Anwani"
    override val addResidence = "Ongeza Makazi"

    // Common strings
    override val add = "Ongeza"
    override val reload = "Pakia upya"
    override val search = "Tafuta"
    override val page = "Ukurasa"
    override val of = "ya"
    override val showing = "Inaonyesha"
    override val noAddress = "Hakuna anwani"
    override val selectGender = "Chagua Jinsia"
    override val age = "Umri"
    override val glossary = "Kamusi"
    override val searchPlaceholder = "Tafuta kwa nambari ya kitambulisho, jina au maelezo mengine..."
    override val addNewResident = "Ongeza Mkazi Mpya"

    // Content descriptions
    override val contentDescAdd = "Ongeza"
    override val contentDescEdit = "Hariri"
    override val contentDescDelete = "Futa"
    override val contentDescSaveChanges = "Hifadhi Mabadiliko"
    override val contentDescReload = "Pakia upya"
    override val contentDescGlossary = "Kamusi"

    override val dependentsInformation = "Taarifa za Wategemezi"
    override val addDependent = "Ongeza Mtegemezi"
    override val editDependent = "Hariri Mtegemezi"
    override val name = "Jina"
    override val surname = "Jina la Ukoo"
    override val genderMale = "Mwanaume"
    override val genderFemale = "Mwanamke"
    override val genderOther = "Nyingine"
    override val toggleDropdown = "Badilisha orodha"
    override val save = "Hifadhi"
    override val cancel = "Ghairi"
    override val edit = "Hariri"
    override val delete = "Futa"
    override val back = "Rudi"
    override val changesSaved = "Mabadiliko yamehifadhiwa kwa mafanikio"

    override val next = "Inayofuata"
    override val previous = "Iliyotangulia"
    override val contentDescCancel = "Ghairi"
    override val contentDescSave = "Hifadhi"
    override val contentDescPrevious = "Iliyotangulia"
    override val contentDescNext = "Inayofuata"

    override val settings = "Mipangilio"
    override val language = "Lugha"
    override val selectLanguage = "Chagua Lugha"
    override val appearance = "Mwonekano"
    override val darkMode = "Hali ya Giza"

    override val resident = "Mkazi"
    override val residents = "Wakazi"
    override val addResident = "Ongeza Mkazi"
    override val editResident = "Hariri Mkazi"
    override val firstName = "Jina la Kwanza"
    override val lastName = "Jina la Mwisho"
    override val dateOfBirth = "Tarehe ya Kuzaliwa"
    override val gender = "Jinsia"
    override val idNumber = "Nambari ya Kitambulisho"

    override val qualifications = "Sifa"
    override val dependents = "Wategemezi"
    override val residence = "Makazi"
    override val employment = "Ajira"

    override val residenceInformation = "Taarifa za Makazi"
    override val street = "Mtaa"
    override val houseNumber = "Nambari ya Nyumba"
    override val suburb = "Mtaa wa Kando"
    override val town = "Mji"
    override val postalCode = "Msimbo wa Posta"
    override val geoCoordinates = "Mahali pa Kijiografia"
    override val landmark = "Alama ya Ardhi"
    override val occupationDate = "Tarehe ya Kuingia"
    override val noResidenceInfo = "Hakuna taarifa za makazi"
    override val editResidence = "Hariri Makazi"
    override val optional = "Hiari"
}

class AmharicStringResources : StringResources {
    override val animal = "እንስሳ"
    override val resource = "ሀብት"
    override val admin = "አስተዳዳሪ"
    override val adminScreen = "የአስተዳዳሪ ማያ"
    override val generatedDocuments = "የተፈጠሩ ሰነዶች"
    override val viewGeneratedDocuments = "የተፈጠሩ ሰነዶችን ይመልከቱ"
    override val generatedDocumentsList = "የተፈጠሩ ሰነዶች ዝርዝር"

    // Form field labels
    override val firstNameLabel = "የመጀመሪያ ስም:"
    override val lastNameLabel = "የአባት ስም:"
    override val dateOfBirthLabel = "የልደት ቀን:"
    override val genderLabel = "ፆታ:"
    override val idNumberLabel = "መታወቂያ ቁጥር:"
    override val phoneNumberLabel = "ስልክ ቁጥር:"
    override val emailLabel = "ኢሜይል:"

    // Form placeholders
    override val enterFirstName = "የመጀመሪያ ስም ያስገቡ"
    override val enterLastName = "የአባት ስም ያስገቡ"
    override val selectDateOfBirth = "የልደት ቀን ይምረጡ"
    override val selectGenderPlaceholder = "ፆታ ይምረጡ"
    override val enterIdNumber = "13 አሃዝ ያለው መታወቂያ ቁጥር ያስገቡ"
    override val enterPhoneNumber = "ስልክ ቁጥር ያስገቡ (ለምሳሌ፣ +27123456789)"
    override val enterEmail = "የኢሜይል አድራሻ ያስገቡ"

    // Validation errors
    override val invalidEmailFormat = "ልክ ያልሆነ የኢሜይል ቅርፀት"
    override val invalidPhoneFormat = "ልክ ያልሆነ የስልክ ቁጥር ቅርፀት"
    override val idNumberMustBe13Digits = "መታወቂያ ቁጥር 13 አሃዝ መሆን አለበት"

    // Date picker
    override val today = "ዛሬ"
    override val month = "ወር"
    override val year = "ዓመት"
    override val selectDate = "ቀን ይምረጡ"

    // Gender dropdown
    override val closeGenderSelection = "የፆታ ምርጫን ዝጋ"
    override val openGenderSelection = "የፆታ ምርጫን ክፈት"
    override val genderMaleDisplay = "ወንድ"
    override val genderFemaleDisplay = "ሴት"

    // Residence
    override val proofOfAddress = "የአድራሻ ማረጋገጫ"
    override val addResidence = "መኖሪያ ጨምር"

    // Common strings
    override val add = "ጨምር"
    override val reload = "እንደገና ጫን"
    override val search = "ፈልግ"
    override val page = "ገጽ"
    override val of = "ከ"
    override val showing = "እያሳየ ነው"
    override val noAddress = "አድራሻ የለም"
    override val selectGender = "ፆታ ይምረጡ"
    override val age = "እድሜ"
    override val glossary = "መዝገበ ቃላት"
    override val searchPlaceholder = "በመታወቂያ ቁጥር፣ በስም ወይም በሌሎች ዝርዝሮች ይፈልጉ..."
    override val addNewResident = "አዲስ ነዋሪ ጨምር"

    // Content descriptions
    override val contentDescAdd = "ጨምር"
    override val contentDescEdit = "አርትዕ"
    override val contentDescDelete = "ሰርዝ"
    override val contentDescSaveChanges = "ለውጦችን አስቀምጥ"
    override val contentDescReload = "እንደገና ጫን"
    override val contentDescGlossary = "መዝገበ ቃላት"

    override val dependentsInformation = "የጥገኞች መረጃ"
    override val addDependent = "ጥገኛ ጨምር"
    override val editDependent = "ጥገኛን አርትዕ"
    override val name = "ስም"
    override val surname = "የአባት ስም"
    override val genderMale = "ወንድ"
    override val genderFemale = "ሴት"
    override val genderOther = "ሌላ"
    override val toggleDropdown = "ድሮፕዳውን ቀይር"
    override val save = "አስቀምጥ"
    override val cancel = "ሰርዝ"
    override val edit = "አርትዕ"
    override val delete = "ሰርዝ"
    override val back = "ተመለስ"
    override val changesSaved = "ለውጦች በተሳካ ሁኔታ ተቀምጠዋል"

    override val next = "ቀጣይ"
    override val previous = "ቀዳሚ"
    override val contentDescCancel = "ሰርዝ"
    override val contentDescSave = "አስቀምጥ"
    override val contentDescPrevious = "ቀዳሚ"
    override val contentDescNext = "ቀጣይ"

    override val settings = "ቅንብሮች"
    override val language = "ቋንቋ"
    override val selectLanguage = "ቋንቋ ይምረጡ"
    override val appearance = "መልክ"
    override val darkMode = "ጨለማ ሁነታ"

    override val resident = "ነዋሪ"
    override val residents = "ነዋሪዎች"
    override val addResident = "ነዋሪ ጨምር"
    override val editResident = "ነዋሪን አርትዕ"
    override val firstName = "የመጀመሪያ ስም"
    override val lastName = "የአባት ስም"
    override val dateOfBirth = "የልደት ቀን"
    override val gender = "ፆታ"
    override val idNumber = "መታወቂያ ቁጥር"

    override val qualifications = "ብቃቶች"
    override val dependents = "ጥገኞች"
    override val residence = "መኖሪያ"
    override val employment = "ስራ"

    override val residenceInformation = "የመኖሪያ መረጃ"
    override val street = "መንገድ"
    override val houseNumber = "የቤት ቁጥር"
    override val suburb = "አካባቢ"
    override val town = "ከተማ"
    override val postalCode = "የፖስታ ኮድ"
    override val geoCoordinates = "የመሬት አቀማመጥ"
    override val landmark = "መለያ ምልክት"
    override val occupationDate = "የመያዣ ቀን"
    override val noResidenceInfo = "የመኖሪያ መረጃ የለም"
    override val editResidence = "መኖሪያን አርትዕ"
    override val optional = "አማራጭ"
}

class YorubaStringResources : StringResources {
    override val animal = "Ẹranko"
    override val resource = "Ohun elo"
    override val admin = "Alakoso"
    override val adminScreen = "Skrini Alakoso"
    override val generatedDocuments = "Awọn Iwe ti a Ṣẹda"
    override val viewGeneratedDocuments = "Wo Awọn Iwe ti a Ṣẹda"
    override val generatedDocumentsList = "Akojọ Awọn Iwe ti a Ṣẹda"

    // Form field labels
    override val firstNameLabel = "Orukọ Kini:"
    override val lastNameLabel = "Orukọ Idile:"
    override val dateOfBirthLabel = "Ọjọ Ibi:"
    override val genderLabel = "Abo tabi Ako:"
    override val idNumberLabel = "Nọmba ID:"
    override val phoneNumberLabel = "Nọmba Fonu:"
    override val emailLabel = "Imeeli:"

    // Form placeholders
    override val enterFirstName = "Tẹ orukọ kini sii"
    override val enterLastName = "Tẹ orukọ idile sii"
    override val selectDateOfBirth = "Yan ọjọ ibi"
    override val selectGenderPlaceholder = "Yan abo tabi ako"
    override val enterIdNumber = "Tẹ nọmba ID onka 13 sii"
    override val enterPhoneNumber = "Tẹ nọmba fonu sii (fun apẹẹrẹ, +27123456789)"
    override val enterEmail = "Tẹ adirẹsi imeeli sii"

    // Validation errors
    override val invalidEmailFormat = "Ọna kika imeeli ti ko tọ"
    override val invalidPhoneFormat = "Ọna kika nọmba fonu ti ko tọ"
    override val idNumberMustBe13Digits = "Nọmba ID gbọdọ jẹ onka 13"

    // Date picker
    override val today = "Oni"
    override val month = "Osu"
    override val year = "Ọdun"
    override val selectDate = "Yan ọjọ"

    // Gender dropdown
    override val closeGenderSelection = "Pa yiyan abo tabi ako"
    override val openGenderSelection = "Ṣi yiyan abo tabi ako"
    override val genderMaleDisplay = "Ọkunrin"
    override val genderFemaleDisplay = "Obinrin"

    // Residence
    override val proofOfAddress = "Ẹri Adirẹsi"
    override val addResidence = "Fi Ibugbe kun"

    // Common strings
    override val add = "Fi kun"
    override val reload = "Tun gbà"
    override val search = "Àwárí"
    override val page = "Ojú ìwé"
    override val of = "ti"
    override val showing = "Fihan"
    override val noAddress = "Ko si adirẹsi"
    override val selectGender = "Yan Abo tabi Ako"
    override val age = "Ọjọ ori"
    override val glossary = "Ìwé ìtumọ̀"
    override val searchPlaceholder = "Àwárí nípa nọmba ID, orúkọ tàbí àwọn alaye míràn..."
    override val addNewResident = "Fi Olugbe Tuntun kun"

    // Content descriptions
    override val contentDescAdd = "Fi kun"
    override val contentDescEdit = "Ṣatunkọ"
    override val contentDescDelete = "Pa rẹ"
    override val contentDescSaveChanges = "Fipamọ Awọn ayipada"
    override val contentDescReload = "Tun gbà"
    override val contentDescGlossary = "Ìwé ìtumọ̀"

    override val dependentsInformation = "Alaye Awọn Ọmọ"
    override val addDependent = "Fi Ọmọ kun"
    override val editDependent = "Ṣatunkọ Ọmọ"
    override val name = "Orukọ"
    override val surname = "Orukọ Idile"
    override val genderMale = "Ọkunrin"
    override val genderFemale = "Obinrin"
    override val genderOther = "Omiiran"
    override val toggleDropdown = "Yi aṣayan pada"
    override val save = "Fipamọ"
    override val cancel = "Fagilee"
    override val edit = "Ṣatunkọ"
    override val delete = "Pa rẹ"
    override val back = "Pada"
    override val changesSaved = "Awọn ayipada ti fipamọ ni ifijišẹ"

    override val next = "Itele"
    override val previous = "Ti tẹlẹ"
    override val contentDescCancel = "Fagilee"
    override val contentDescSave = "Fipamọ"
    override val contentDescPrevious = "Ti tẹlẹ"
    override val contentDescNext = "Itele"

    override val settings = "Ètò"
    override val language = "Èdè"
    override val selectLanguage = "Yan Èdè"
    override val appearance = "Irisi"
    override val darkMode = "Ipo Dudu"

    override val resident = "Olugbe"
    override val residents = "Awọn Olugbe"
    override val addResident = "Fi Olugbe kun"
    override val editResident = "Ṣatunkọ Olugbe"
    override val firstName = "Orukọ Kini"
    override val lastName = "Orukọ Idile"
    override val dateOfBirth = "Ọjọ Ibi"
    override val gender = "Abo tabi Ako"
    override val idNumber = "Nọmba ID"

    override val qualifications = "Awọn Ẹkọ"
    override val dependents = "Awọn Ọmọ"
    override val residence = "Ibugbe"
    override val employment = "Iṣẹ"

    override val residenceInformation = "Alaye Ibugbe"
    override val street = "Opopona"
    override val houseNumber = "Nọmba Ile"
    override val suburb = "Agbegbe"
    override val town = "Ilu"
    override val postalCode = "Koodu Ifiweranṣẹ"
    override val geoCoordinates = "Ipo Ile-aye"
    override val landmark = "Ami Ile"
    override val occupationDate = "Ọjọ Igbese"
    override val noResidenceInfo = "Ko si alaye ibugbe"
    override val editResidence = "Ṣatunkọ Ibugbe"
    override val optional = "Aṣayan"
}

object StringResourcesManager {
    fun getStringResources(locale: SupportedLanguage): StringResources {
        return when (locale) {
            SupportedLanguage.ENGLISH -> EnglishStringResources()
            SupportedLanguage.SETSWANA -> SetswanaStringResources()
            SupportedLanguage.XHOSA -> XhosaStringResources()
            SupportedLanguage.SEPEDI -> SepediStringResources()
            SupportedLanguage.SHONA -> ShonaStringResources()
            SupportedLanguage.SWAHILI -> SwahiliStringResources()
            SupportedLanguage.AMHARIC -> AmharicStringResources()
            SupportedLanguage.YORUBA -> YorubaStringResources()
        }
    }

    fun getCurrentStringResources(): StringResources {
        return getStringResources(LocaleManager.getCurrentLocale())
    }
}
