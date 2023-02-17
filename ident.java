public static boolean ident(String s) {
        char ch;
        int len = s.length();
        for (int i=0; i<len; i++) {
            ch = s.charAt(i);
            if (ch < 97 || ch > 123) return false;
        }
        return true;
    }
