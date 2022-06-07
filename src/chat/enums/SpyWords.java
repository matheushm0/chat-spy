package chat.enums;

public enum SpyWords {

    ESPI�O("espi�o"),
    SEGREDO("segredo"),
    CONFIDENCIAL("confidencial");
	
	private String keyWord;
	
	SpyWords(String keyWord) {
		this.keyWord = keyWord;
	}

	public static boolean contains(String message) {
		
	    for (SpyWords sw : SpyWords.values()) {
	        if (message.toLowerCase().contains(sw.keyWord)) {
	            return true;
	        }
	    }
		
		return false;
	}

}
