1 - baixar chromeDriver no link https://googlechromelabs.github.io/chrome-for-testing/#stable (no meu caso, foi o chromedriver win64)
2 - extrair para alguma pasta, recomendação: C:\chromedriver-win64
3 - adicionar o path de onde vc colocou o chromedriver nas "variaveis do sistema"
4 - baixar o projeto
5 - dar um mvn clean install, baixando as dependencias
6 - ir em application.properties, colocar seu username + password do izeus
7 - executar o projeto

observação:
caso queira, pode por um debugger na linha 227 do arquivo AutomacaoPonto.java  executar em modo debugger... assim você irá verificar se o tempo foi registrado corretamente
