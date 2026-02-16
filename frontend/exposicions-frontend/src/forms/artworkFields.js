export const artworkSections = [
    {
        title: "Obra",
        fields: [
            { name: "title", label: "Títol", type: "text", required: true },
            { name: "yearInt", label: "Any", type: "number" },
            { name: "dateText", label: "Data (text)", type: "text" },
            { name: "description", label: "Descripció", type: "textarea" },
            { name: "fileUrl", label: "URL fitxer", type: "text" },
            { name: "web", label: "Web", type: "text" },
            { name: "imageAtCccb", label: "Imatge al CCCB?", type: "checkbox" },
        ],
    },
    {
        title: "Autor",
        fields: [
            { name: "authorName", label: "Autor nom", type: "text" },
            { name: "authorSurname", label: "Autor cognom", type: "text" },
            { name: "author", label: "Autor (format lliure)", type: "text" },
            { name: "credit", label: "Crèdit", type: "text" },
        ],
    },
    {
        title: "Identificadors",
        fields: [
            { name: "lenderNo", label: "Nº prestador", type: "number" },
            { name: "workNo", label: "Nº obra", type: "number" },
            { name: "inventoryNo", label: "Nº inventari", type: "text" },
            { name: "elementsNo", label: "Nº d'elements", type: "number" },
        ],
    },
    {
        title: "Classificació",
        fields: [
            { name: "workType", label: "Tipus d'obra", type: "text" },
            { name: "techniqueMaterial", label: "Tècnica/material", type: "text" },
            { name: "originalReproAv", label: "Original/Repro/AV", type: "text" },
            { name: "reproType", label: "Tipus repro", type: "text" },
            { name: "section", label: "Apartat", type: "text" },
            { name: "ambit", label: "Àmbit", type: "text" },
            { name: "subambit", label: "Subàmbit", type: "text" },
            { name: "thematicGroupOk", label: "Grup temàtic OK", type: "text" },
            { name: "subSectionOk", label: "Subapartat OK", type: "text" },
        ],
    },
    {
        title: "Mides",
        fields: [
            { name: "height", label: "Alçada", type: "number" },
            { name: "width", label: "Amplada", type: "number" },
            { name: "depth", label: "Fondària", type: "number" },
            { name: "framedDimensions", label: "Mides amb marc", type: "text" },
            { name: "dimensionsText", label: "Mides (text)", type: "text" },
            { name: "sizeNotes", label: "Observacions mides", type: "textarea" },
        ],
    },
    {
        title: "Gestió i estat",
        fields: [
            { name: "managementStatus", label: "Estat de gestió", type: "text" },
            { name: "loanOk", label: "OK préstec", type: "text" },
            { name: "reviewed", label: "Revisat", type: "text" },
            { name: "managementNotes", label: "Observacions gestió", type: "textarea" },
            { name: "otherInfo", label: "Altres infos obra", type: "textarea" },
            { name: "optionsText", label: "Opcions", type: "text" },
            { name: "defExposed", label: "Def. Exposada", type: "text" },
        ],
    },
    {
        title: "Conservació / Transport / Assegurances",
        fields: [
            { name: "conservationNotes", label: "Observacions conservació", type: "textarea" },
            { name: "transportNotes", label: "Observacions transport", type: "text" },
            { name: "insuranceNotes", label: "Observacions assegurances", type: "text" },
            { name: "insurance", label: "Assegurança", type: "text" },
            { name: "tender", label: "Licitació", type: "textarea" },
            { name: "finalFormat", label: "Format final", type: "textarea" },
            { name: "valuation", label: "Valoració", type: "number" },
            { name: "currency", label: "Divisa", type: "text" },
        ],
    },
    {
        title: "Embalatge / logística",
        fields: [
            { name: "packaging", label: "Embalatge", type: "text" },
            { name: "packagingDimensions", label: "Mides embalatge", type: "text" },
            { name: "entryDeliveryNote", label: "Albarà d'entrada CCCB", type: "text" },
            { name: "frameAtCccb", label: "Emmarcar al CCCB", type: "text" },
            { name: "showcasePedestal", label: "Vitrina/peanya", type: "text" },
            { name: "currentLocation", label: "Ubicació actual", type: "text" },
            { name: "placement", label: "Col·locació", type: "text" },
        ],
    },
    {
        title: "Recollida / retorn",
        fields: [
            { name: "pickupAddress", label: "Adreça recollida", type: "textarea" },
            { name: "pickupCityCode", label: "Codi i ciutat recollida", type: "text" },
            { name: "pickupCountry", label: "País recollida", type: "text" },
            { name: "returnAddress", label: "Adreça retorn", type: "textarea" },
            { name: "returnCityCode", label: "Codi i ciutat retorn", type: "text" },
            { name: "returnCountry", label: "País retorn", type: "text" },
        ],
    },
    {
        title: "Catàleg / imatges",
        fields: [
            { name: "inCatalog", label: "A catàleg?", type: "text" },
            { name: "catalogNotes", label: "Observacions catàleg", type: "textarea" },
            { name: "labelInfo", label: "Info per cartel·la", type: "text" },
            { name: "hiResImage", label: "Imatge alta resolució (URL o text)", type: "text" },
        ],
    },
    {
        title: "AV",
        fields: [
            { name: "avNo", label: "Nº AV", type: "text" },
            { name: "avSupport", label: "Suport AV", type: "text" },
        ],
    },
];
