import java.io.*;
import java.util.*;

public class Scanner {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        StringTokenizer stringTokenizer;

        // 소스 파일 불러오기
        System.out.print("소스 코드 파일 경로 입력 > ");
        String filePath = bufferedReader.readLine();    // 소스 코드 파일 경로 입력 받기
        FileReader fileReader = new FileReader(filePath);
        bufferedReader = new BufferedReader(fileReader);

        // op table 불러오기
        FileReader opTableFile = new FileReader("C:\\Users\\laila\\OneDrive\\문서\\단국대학교\\4학년 2학기\\오토마타와 컴파일러\\op_table.txt");
        BufferedReader br = new BufferedReader(opTableFile);
        Map<Integer, String> opTable = new HashMap<>();
        String operation;
        while ((operation = br.readLine()) != null) {
            stringTokenizer = new StringTokenizer(operation);
            opTable.put(Integer.valueOf(stringTokenizer.nextToken()), stringTokenizer.nextToken());
        }

        // 실행 결과 파일
        File file = new File("C:\\Users\\laila\\OneDrive\\문서\\단국대학교\\4학년 2학기\\오토마타와 컴파일러\\result_file.txt");
        FileWriter resultWriter = new FileWriter(file);

        // id_table(symbol_table) 파일
        File idFile = new File("C:\\Users\\laila\\OneDrive\\문서\\단국대학교\\4학년 2학기\\오토마타와 컴파일러\\symbol_table.txt");
        FileWriter idWriter = new FileWriter(idFile);
        int count = 0;

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            // 주석인 경우, scan도 하지 않고 token 발급도 하지 않음
            int index;
            // 주석을 포함하고 있으면 그 부분은 스캔하지 않음
            if (line.contains("/?") && line.contains("?/")) {
                int beginIdx = line.indexOf("/?");
                int endIdx = line.indexOf("?/");
                String annotation = line.substring(beginIdx, endIdx+2);
                line = line.replace(annotation, "");
            }
            // 한 줄 전체가 주석인 경우 다음 줄로 넘어감
            if (line.equals("")) {
                continue;
            }

            // 주석이 아닌 경우 scan 시작(주석인 경우는 주석인 부분을 제외하고 scan)
            stringTokenizer = new StringTokenizer(line);
            List<String> tokenList = new ArrayList<>();
            int tokenIdx = 0;
            while (stringTokenizer.hasMoreTokens()) {
                tokenList.add(stringTokenizer.nextToken());
                tokenIdx++;
            }

            index = 0;
            while (index < tokenList.size()) {
                String token = tokenList.get(index);
                int tokenNum;
                // 첫 문자가 대문자인 경우
                if (token.charAt(0) >= 65 && token.charAt(0) <= 90) {
                    // 첫 문자가 대문자일 경우, 예약어인지 확인
                    if (opTable.containsValue(token)) {
                        tokenNum = getKey(opTable, token);  // token의 토큰 번호
                        resultWriter.write(tokenNum + " " + token + "\n");
                        index++;
                    }
                    // 첫 문자가 대문자인데, 예약어가 아니라면 error
                    else {
                        bufferedWriter.write("예약어 형식에 맞지 않습니다.");
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        return;
                    }
                }
                // 첫 문자가 소문자인 경우
                else if (token.charAt(0) >= 97 && token.charAt(0) <= 122) {
                    // 토큰이 소문자로 시작하면, 사용자 정의어의 형식에 맞는지 확인
                    String id = String.valueOf(token.charAt(0));
                    int i = 1;
                    while (i < token.length()) {
                        // id 토큰에 문자가 등장할 경우, substring으로 쪼개기
                        if (isEndOfId(token.charAt(i))) {
                            tokenNum = 3;
                            resultWriter.write(tokenNum + " " + id + "\n");       // 실행 결과 파일에 입력
                            idWriter.write(count + " " + id + "\n");    // 심볼 테이블에 입력
                            count++;    // 사용자 정의어 개수 count
                            String newToken = token.substring(i);
                            tokenList.set(index, newToken);
                            break;
                        }
                        id = id + token.charAt(i);
                        i++;
                    }
                    // 해당 토큰에 문자가 등장하지 않고 모두 사용자 정의어(id)인 경우
                    if (i == token.length()) {
                        tokenNum = 3;
                        resultWriter.write(tokenNum + " " + id + "\n");
                        idWriter.write(count + " " + id + "\n");
                        count++;
                        index++;
                    }
                }
                // 첫 문자가 숫자인 경우(0-9)
                else if (token.charAt(0) >= 48 && token.charAt(0) <= 57) {
                    StringBuilder num = new StringBuilder(String.valueOf(token.charAt(0)));
                    tokenNum = 5;
                    int i = 1;
                    while (i < token.length()) {
                        if (token.charAt(i) >= 48 && token.charAt(i) <= 57) {
                            num.append(token.charAt(i));
                            i++;
                            continue;
                        }
                        // 실수일 경우
                        else if (token.charAt(i) == '.') {
                            tokenNum = 6;
                            num.append(token.charAt(i));
                            i++;
                            if (token.charAt(i) >= 48 && token.charAt(i) <= 57) {
                                num.append(token.charAt(i));
                                i++;
                                while (i < token.length()) {
                                    if (token.charAt(i) == 'e') break;    // 아래 if문 실행
                                    else if (token.charAt(i) >= 48 && token.charAt(i) <= 57) {
                                        num.append(token.charAt(i));
                                        i++;
                                    }
                                    // e, 0-9가 아닌 문자가 나왔을 경우, substring으로 쪼개기
                                    else {
                                        resultWriter.write("6 " + num + "\n");
                                        String newToken = token.substring(i);
                                        tokenList.set(index, newToken);
                                        i = token.length();     // 바깥의 while문을 빠져나가기 위해
                                        break;
                                    }
                                }
                                resultWriter.write("6 " + num + "\n");    // 실수 토큰
                                index++;
                            }
                            // 소수점 뒤에는 반드시 1개 이상의 숫자가 나와야한다.
                            else {
                                bufferedWriter.write("실수 형식에 맞지 않습니다.");
                                bufferedWriter.flush();
                                bufferedWriter.close();
                                return;
                            }
                        }
                        if (token.charAt(i) == 'e') {
                            tokenNum = 6;
                            num.append(token.charAt(i));
                            i++;
                            if (token.charAt(i) == '+' | token.charAt(i) == '-') {
                                num.append(token.charAt(i));
                                i++;
                                while (i < token.length()) {
                                    if (token.charAt(i) >= 48 && token.charAt(i) <= 57) {
                                        num.append(token.charAt(i));
                                        i++;
                                    }
                                    else {
                                        bufferedWriter.write("실수 형식에 맞지 않습니다.");
                                        bufferedWriter.flush();
                                        bufferedWriter.close();
                                        return;
                                    }
                                }
                                resultWriter.write("6 " + num + "\n");    // 실수 토큰
                                index++;
                            }
                            // e 다음에 +/-가 오지 않는 경우 실수 형식에 맞지 않음
                            else {
                                bufferedWriter.write("실수 형식에 맞지 않습니다.");
                                bufferedWriter.flush();
                                bufferedWriter.close();
                                return;
                            }
                        }
                        // 숫자,.,e 외의 문자가 나올 경우
                        else {
                            resultWriter.write(tokenNum + " " + num + "\n");
                            if (i < token.length()) {
                                token = token.substring(i);
                                tokenList.set(index, token);
                                continue;
                            }
                            index++;
                        }
                    }

                }

                // 연산자 토큰 식별
                else if (token.charAt(0) == '!') {
                    String op = String.valueOf(token.charAt(0));
                    if (token.charAt(1) == '=') {
                        op = op + token.charAt(1);
                        resultWriter.write("20 " + op + "\n");
                        String newToken = token.substring(2);
                        tokenList.set(index, newToken);
                    }
                    else {
                        resultWriter.write("16 " + op + "\n");
                        String newToken = token.substring(1);
                        tokenList.set(index, newToken);
                    }
                }

                else if (token.charAt(0) == '%') {
                    String op = String.valueOf(token.charAt(0));
                    resultWriter.write("14 " + op + "\n");
                    String newToken = token.substring(1);
                    tokenList.set(index, newToken);
                }

                else if (token.charAt(0) == '&') {
                    String op = String.valueOf(token.charAt(0));
                    if (token.length() > 1) {
                        if (token.charAt(1) == '&') {
                            op = op + token.charAt(1);
                            resultWriter.write("17 " + op + "\n");
                            if (token.length() > 2) {
                                String newToken = token.substring(2);
                                tokenList.set(index, newToken);
                                continue;
                            }
                            index++;
                            continue;
                        }
                    }
                    // & 다음에 &이 나오지 않으면 error
                    else {
                        bufferedWriter.write("연산자 형식에 맞지 않습니다.");
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        return;
                    }
                }

                else if (token.charAt(0) == '*') {
                    String op = String.valueOf(token.charAt(0));
                    resultWriter.write("12 " + op + "\n");
                    String newToken = token.substring(1);
                    tokenList.set(index, newToken);
                }

                else if (token.charAt(0) == '+') {
                    String op = String.valueOf(token.charAt(0));
                    resultWriter.write("10 " + op + "\n");
                    String newToken = token.substring(1);
                    tokenList.set(index, newToken);
                }

                else if (token.charAt(0) == '-') {
                    String op = String.valueOf(token.charAt(0));
                    resultWriter.write("11 " + op + "\n");
                    String newToken = token.substring(1);
                    tokenList.set(index, newToken);
                }

                else if (token.charAt(0) == '<') {
                    String op = String.valueOf(token.charAt(0));
                    if (token.length() > 1) {
                        if (token.charAt(1) == '=') {
                            op = op + token.charAt(1);
                            resultWriter.write("23 " + op + "\n");
                            if (token.length() > 2) {
                                String newToken = token.substring(2);
                                tokenList.set(index, newToken);
                                continue;
                            }
                            index++;
                            continue;
                        }
                    }
                    resultWriter.write("21 " + op + "\n");
                    if (token.length() > 1) {
                        String newToken = token.substring(1);
                        tokenList.set(index, newToken);
                        continue;
                    }
                    index++;
                }

                else if (token.charAt(0) == '=') {
                    String op = String.valueOf(token.charAt(0));
                    if (token.length() > 1) {
                        if (token.charAt(1) == '=') {
                            op = op + token.charAt(1);
                            resultWriter.write("19 " + op + "\n");
                            if (token.length() > 2) {
                                String newToken = token.substring(2);
                                tokenList.set(index, newToken);
                                continue;
                            }
                            index++;
                            continue;
                        }
                    }
                    resultWriter.write("15 " + op + "\n");
                    if (token.length() > 1) {
                        String newToken = token.substring(1);
                        tokenList.set(index, newToken);
                        continue;
                    }
                    index++;
                }

                else if (token.charAt(0) == '>') {
                    String op = String.valueOf(token.charAt(0));
                    if (token.length() > 1) {
                        if (token.charAt(1) == '=') {
                            op = op + token.charAt(1);
                            resultWriter.write("24 " + op + "\n");
                            if (token.length() > 2) {
                                String newToken = token.substring(2);
                                tokenList.set(index, newToken);
                                continue;
                            }
                            index++;
                            continue;
                        }
                    }
                    resultWriter.write("22 " + op + "\n");
                    if (token.length() > 1) {
                        String newToken = token.substring(1);
                        tokenList.set(index, newToken);
                        continue;
                    }
                    index++;
                }

                else if (token.charAt(0) == '|') {
                    String op = String.valueOf(token.charAt(0));
                    if (token.length() > 1) {
                        if (token.charAt(1) == '|') {
                            op = op + token.charAt(1);
                            resultWriter.write("18 " + op + "\n");
                            if (token.length() > 2) {
                                String newToken = token.substring(2);
                                tokenList.set(index, newToken);
                                continue;
                            }
                            index++;
                            continue;
                        }
                    }
                    // '|' 뒤에 '|'가 나오지 않으면 error
                    else {
                        bufferedWriter.write("연산자 형식에 맞지 않습니다.");
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        return;
                    }
                }

                // 특수 기호 식별
                else if (opTable.containsValue(String.valueOf(token.charAt(0)))) {
                    int key = getKey(opTable, String.valueOf(token.charAt(0)));
                    resultWriter.write(key + " " + token.charAt(0) + "\n");
                    if (token.length() > 1) {
                        String newToken = token.substring(1);
                        tokenList.set(index, newToken);
                    }
                    else index++;
                }
                resultWriter.flush();
                idWriter.flush();
            }
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        resultWriter.close();
        idWriter.close();
    }

    // hashmap에서 value에 해당하는 key가 존재하는지 확인, 존재한다면 key return
    public static <K, V> K getKey(Map<K, V> map, V value) {
        // 찾을 hashmap 과 주어진 단서 value
        for (K key : map.keySet()) {
            if (value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }

    // a-z, _, 0-9, A-Z가 아님 확인
    public static boolean isEndOfId(char ch) {
        boolean result = false;
        if (ch >= 65 && ch <= 90);  // A-Z
        else if (ch >= 97 && ch <= 122);    // a-z
        else if (ch == '_');        // _
        else if (ch >= 48 && ch <= 57); // 0-9
        else result = true;
        return result;
    }

}
