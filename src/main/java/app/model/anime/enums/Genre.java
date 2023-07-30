package app.model.anime.enums;

public enum Genre {
    ACTION(1,"Action"),

    ADVENTURE(2,  "Adventure"),

    RACING(3,  "Racing"),

    COMEDY(4,  "Comedy"),
    
    AVANT_GARDE(5,  "Avant Garde"),

    MYTHOLOGY(6,  "Mythology"),
    
    MYSTERY(7,  "Mystery"),

    DRAMA(8,  "Drama"),

    ECCHI(9,  "Ecchi"),

    FANTASY(10,  "Fantasy"),

    STRATEGY_GAME(11,  "Strategy Game"),

    HENTAI(12,  "Hentai"),

    HISTORICAL(13,  "Historical"),
    
    HORROR(14, "Horror"),

    KIDS(15,  "Kids"),

    MARTIAL_ARTS(17,  "Martial Arts"),
    
    MECHA(18,  "Mecha"),

    MUSIC(19, "Music"),

    PARODY(20,  "Parody"),
    
    SAMURAI(21,  "Samurai"),
    
    ROMANCE(22,  "Romance"),

    SCHOOL(23,  "School"),

    SCI_FI(24,  "Sci-Fi"),
    
    SHOUJO(25,  "Shoujo"),
    
    SHOUNEN(27,  "Shounen"),

    SPACE(29,  "Space"),
    
    SPORTS(30,  "Sports"),
    
    SUPERPOWER(31, "Super Power"),

    VAMPIRE(32,  "Vampire"),

    HAREM(35,  "Harem"),

    SLICE_OF_LIFE(36,  "Slice of Life"),
    
    SUPERNATURAL(37,  "Supernatural"),
    
    MILITARY(38,  "Military"),
    
    DETECTIVE(39,  "Detective"),
    
    PSYCHOLOGICAL(40,  "Psychological"),

    SUSPENSE(41,  "Suspense"),
    
    SEINEN(42,  "Seinen"),

    JOSEI(43,  "Josei"),
    
    AWARD_WINNING(46,  "Award Winning"),
    
    GOURMET(47,  "Gourmet"),

    WORKPLACE(48,  "Workplace"),

    ADULT_CAST(50,  "Adult Cast"),

    ANTHROPOMORPHIC(51,  "Anthropomorphic"),
    
    CGDCT(52,  "CGDCT"),

    COMBAT_SPORTS(54,  "Combat Sports"),

    DELINQUENTS(55, "Delinquents"),

    EDUCATIONAL(56,  "Educational"),

    GAG_HUMOR(57,  "Gag Humor"),

    GORE(58,  "Gore"),
    
    HIGH_STAKES_GAME(59,  "High Stakes Game"),
    
    ISEKAI(62,  "Isekai"),

    IYASHIKEI(63,  "Iyashikei"),

    LOVE_POLYGON(64, "Love Polygon"),

    MAGICAL_SEX_SHIFT(65, "Magical Sex Shift"),

    MAHOU_SHOUJO(66,  "Mahou Shoujo"),

    MEDICAL(67,  "Medical"),

    ORGANIZED_CRIME(68,  "Organized Crime"),
    
    OTAKU_CULTURE(69,  "Otaku Culture"),

    PERFORMING_ARTS(70,  "Performing Arts"),

    PETS(71,  "Pets"),

    REINCARNATION(72,  "Reincarnation"),

    REVERSE_HAREM(73,  "Reverse Harem"),

    ROMANTIC_SUBTEXT(74,  "Romantic Subtext"),
    
    SHOWBIZ(75,  "Showbiz"),

    SURVIVAL(76,  "Survival"),

    TEAM_SPORTS(77,  "Team Sports"),

    TIME_TRAVEL(78,  "Time Travel"),

    VIDEO_GAME(79, "Video Game"),

    VISUAL_ARTS(80, "Visual Arts"),

    CROSSDRESSING(81, "Crossdressing");


    public final int id;
    public final String name;

    Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}