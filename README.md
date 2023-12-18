# DFA Scanner
JAVA를 이용하여 간단한 Scanner(어휘분석기) 구현
<br><br>

## 📝 프로그램 소개
`∑={a, b}`
<br>
`G(L) = (a+b)*bbb(a+b)*`
<br><br>
**문자열을 scan하면서 위 문법에 맞는 문자열은 accept하고, 문법에 맞지 않는 문자열은 reject하는 프로그램**
<br><br>

## 🖥 간단한 코드 설명
```java
if (result) bufferedWriter.write("문법에 맞는 문자열입니다.");
else bufferedWriter.write("문법에 맞지 않는 문자열입니다.");
```
`boolean result`를 **false**로 초기화하고, `substring 'bbb'`가 문자열에 포함되는 경우에 **true**로 바꾸는 로직을 이용하였다.
<br><br>
`substring 'bbb'`를 찾는 로직은 `if-else`구문을 사용하였고, 문자열을 스캔하는 도중에 연속된 b가 3개 나오면 그 뒤에 어떤 문자가 나오든 이미 문법에 맞는 문자열임이 확실하기 때문에 `boolean result`를 **true**로 설정하고 반복문을 빠져나오게 설계하였다.
<br><br>

## 🔎 예시
- 문법에 맞는 경우
```
문자열 입력 > abbbbabababb
문법에 맞는 문자열입니다.
```
<br>

- 문법에 맞지 않는 경우
```
문자열 입력 > ababaaabb
문법에 맞지 않는 문자열입니다.
```
