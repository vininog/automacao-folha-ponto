- baixar chromeDriver no link https://googlechromelabs.github.io/chrome-for-testing/#stable (no meu caso, foi o chromedriver win64)
- extrair para alguma pasta, recomendação: C:\chromedriver-win64
- adicionar o path de onde vc colocou o chromedriver nas "variaveis do sistema"
- baixar o projeto
- dar um mvn clean install, baixando as dependencias
- ir em application.properties, colocar seu username + password do izeus
- executar o projeto

observação:
caso queira, pode por um debugger na linha 227 do arquivo AutomacaoPonto.java  executar em modo debugger... assim você irá verificar se o tempo foi registrado corretamente

!! importante !!
- Antes de executar o script, lance as exceções manualmente (ferias, falta abonada, atestado medico e talz)
