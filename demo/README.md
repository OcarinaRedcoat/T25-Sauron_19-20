![IST Lgo](IST_C_RGB_POS.png "IST Logo")

# **Guião de Demonstração**

Sistemas Distribuídos<br>2019-2020

## **Autores : Grupo T25** 

Em conformidade com o enunciado do projeto de Sistemas Distribuídos, serve o presente documento como guia de demonstração do mesmo, apresentando casos de utilização, passo a passo, que demonstram as funcionalidades de todo o trabalho desenvolvido.

Abaixo encontra-se um índice por forma a facilitar a locomoção dentro do documento.

## **Índice**  
**1. Preparação do Sistema**  
**&nbsp;&nbsp;&nbsp;1.1. Compilação do Projeto**  
**&nbsp;&nbsp;&nbsp;1.2. Silo**  
**&nbsp;&nbsp;&nbsp;1.3. Eye**  
**2. Teste das Operações**  
**&nbsp;&nbsp;&nbsp;2.1. *cam_join***  
**&nbsp;&nbsp;&nbsp;2.2. *cam_info***  
**&nbsp;&nbsp;&nbsp;2.3. *report***  
**&nbsp;&nbsp;&nbsp;2.4. *track***  
**&nbsp;&nbsp;&nbsp;2.5. *track_match***  
**&nbsp;&nbsp;&nbsp;2.6. *trace***   
**3. Replicação e Tolerância a Faltas**     
**4. Considerações Finais**

<br>

## **1. Preparação do Sistema**
Para testar a aplicação, bem como todos os seus componentes, é necessário a preparação de um ambiente com dados para que se proceda à verificação dos testes e casos de utilização.  
***Observação***: Em todos os exemplos, o símbolo “$” representa a Shell do sistema operativo. Quando este aparece prefixado com uma expressão "X-$", X indica qual o componente da aplicação (eye-$ representa uma *shell* do componente *eye*).


### **1.1. Compilação do Projeto**
Em primeiro lugar, é necessário instalar todas as dependências indispensáveis para os componentes da aplicação: o *silo* e os clientes (eye e spotter), bem como compilar os próprios. Para isso, basta ir à diretoria *root* do projeto e correr o seguinte comando:

```
$ mvn clean install -DskipTests    
```

Após o comando acima, já é possível analisar se o projeto compila na íntegra.


### **1.2. Silo**
Para que se procedam aos testes seguintes, é necessário o servidor *silo* estar a correr. Para tal, é necessário ir à diretoria silo-server e executar:

```
$ mvn exec:java
```

Este comando vai colocar o servidor silo no endereço localhost e na porta 8080.


### **1.3. Eye**
Vamos agora registar X câmaras e as respetivas observações. Cada câmara vai ter o seu ficheiro de entrada próprio, com observações já predefinidas. Para isso, basta ir à diretoria eye e correr os seguintes comandos:
# VER OS EXEMPLOS!!!!!!!!!!!!!!!

```
eye-$ localhost 8080 Tagus 38.737613 -9.303164 < eye1.txt
eye-$ localhost 8080 Alameda 30.303164 -10.737613 < eye2.txt
eye-$ localhost 8080 Lisboa 32.737613 -15.303164 < eye3.txt
```

Depois de executar os comandos acima, já temos tudo o que é necessário para testar o sistema na íntegra.

<br>

<br>

## **2. Teste das Operações**

Nesta secção, vamos correr os comandos necessários para testar todas as operações. Cada subsecção é respetiva a cada operação presente no *silo*.

### **2.1. *cam_join***
Repare-se que esta operação já foi testada aquando da preparação do ambiente: ao executar a secção 1.3, estamos efetivamente a registar três câmaras utilizando a operação *cam_join*.   
No entanto, é ainda necessário testar algumas restrições, apresentadas de seguida.


**2.1.1. Teste de câmaras com nome duplicado e coordenadas distintas**  
O servidor, ao ler esta ação, deve rejeitá-la. Para tal, executa-se um *eye* com o seguinte comando:

```
eye-$ localhost 8080 Tagus 10.0 10.0
```


**2.1.2. Teste do tamanho de um nome**  
O servidor deve rejeitar esta operação. Para isso, basta executar um *eye* com o seguinte comando:

```
eye-$ localhost 8080 ab 10.0 10.0
eye-$ localhost 8080 abcdefghijklmnop 10.0 10.0
```

**2.1.2. Teste de coordenada errada**

# A FAZER

<br>

### **2.2. *cam_info***

# NAO FAÇO IDEIA

### **2.3. *report***

Esta operação também já foi testada aquando da preparação do ambiente: ao enviar o conteúdo dos ficheiros para o programa, com o operador de redirecionamento "<", estamos a introduzir observações através da operação *report*. 

No entanto, falta testar o comando *zzz*. Note-se que na preparação (nomeadamente no ficheiro *eye1.txt*) foi adicionada informação que permite testar este mesmo comando. 

**2.3.1. Teste do comando *zzz***   
Para este teste, basta abrir um cliente *spotter* com:

```
spotter-$ localhost 8080
```

e correr o comando seguinte:

```
> trail car 00AA00
```

O resultado desta operação deve ser duas observações registadas pela câmara Tagus com um intervalo de mais ou menos cinco segundos.

<br>

### **2.4. *track***
Esta operação vai ser testada no cliente *spotter* utilizando o comando *spot* com um identificador.

**2.4.1. Teste com uma pessoa não avistada**  
Este teste deve devolver vazio, visto que a pessoa não foi avistada por nenhuma das câmaras:

```
> spot person 14388236
```

**2.4.2. Teste com uma pessoa avistada por uma câmara**  
Este teste deve devolver a observação mais recente da pessoa escolhida:

```
> spot person 123456789
person,123456789,<timestamp>,Alameda,30.303164,-10.737613
```

**2.4.3. Teste com um carro não avistado**  
Este teste deve devolver vazio, visto que o carro não foi avistado por nenhuma das câmaras:

```
> spot car FF00VX
```

**2.4.4. Teste com um carro avistado por uma câmara**  
Este teste deve devolver a observação mais recente do carro escolhido:

```
> spot car 20SD21
car,20SD21,<timestamp>,Alameda,30.303164,-10.737613
```

<br>

### **2.5. *track_match***
Esta operação vai ser testada no cliente *spotter* utilizando o comando *spot* com um fragmento de identificador.

**2.5.1. Teste com uma pessoa não avistada**  
Este teste deve devolver vazio, visto que a pessoa não foi avistada por nenhuma das câmaras:

```
> spot person 143882*
```

**2.5.2. Testes com uma pessoa avistda**  
Estes testes devem devolver a observação mais recente para cada pessoa encontrada, sem nenhuma ordenação específica:

```
> spot person 111*
person,111111000,<timestamp>,Tagus,38.737613,-9.303164

> spot person *000
person,111111000,<timestamp>,Tagus,38.737613,-9.303164

> spot person 111*000
person,111111000,<timestamp>,Tagus,38.737613,-9.303164
```

**2.5.3. Testes com duas ou mais pessoas avistadas**  
Este teste deve devolver a observação mais recente para cada pessoa encontrada, sem nenhuma ordenação específica:

```
> spot person 123*
person,123111789,<timestamp>,Alameda,30.303164,-10.737613
person,123222789,<timestamp>,Alameda,30.303164,-10.737613
person,123456789,<timestamp>,Alameda,30.303164,-10.737613

> spot person *789
person,123111789,<timestamp>,Alameda,30.303164,-10.737613
person,123222789,<timestamp>,Alameda,30.303164,-10.737613
person,123456789,<timestamp>,Alameda,30.303164,-10.737613

> spot person 123*789
person,123111789,<timestamp>,Alameda,30.303164,-10.737613
person,123222789,<timestamp>,Alameda,30.303164,-10.737613
person,123456789,<timestamp>,Alameda,30.303164,-10.737613
```

**2.5.4. Teste com um carro não avistada**    
Este teste deve devolver vazio, visto que o carro não foi avistada por nenhuma das câmaras:

```
> spot person K*
```


**2.5.5. Testes com um carro avistado**  
Este teste deve devolver a observação mais recente para cada carro encontrado, sem nenhuma ordenação específica:

```
> spot car 00A*
car,00AA00,<timestamp>,Tagus,38.737613,-9.303164

> spot car *A00
car,00AA00,<timestamp>,Tagus,38.737613,-9.303164

> spot car 00*00
car,00AA00,<timestamp>,Tagus,38.737613,-9.303164
```

**2.5.6. Testes com dois ou mais carros:**  
Estes testes devem devolver a observação mais recente para cada carro encontrado, sem nenhuma ordenação específica:

```
> spot car 20SD*
car,20SD20,<timestamp>,Alameda,30.303164,-10.737613
car,20SD21,<timestamp>,Alameda,30.303164,-10.737613
car,20SD22,<timestamp>,Alameda,30.303164,-10.737613

> spot car *XY20
car,66XY20,<timestamp>,Lisboa,32.737613,-15.303164
car,67XY20,<timestamp>,Alameda,30.303164,-10.737613
car,68XY20,<timestamp>,Tagus,38.737613,-9.303164

> spot car 19SD*9
car,19SD19,<timestamp>,Lisboa,32.737613,-15.303164
car,19SD29,<timestamp>,Lisboa,32.737613,-15.303164
car,19SD39,<timestamp>,Lisboa,32.737613,-15.303164
car,19SD49,<timestamp>,Lisboa,32.737613,-15.303164
car,19SD59,<timestamp>,Lisboa,32.737613,-15.303164
car,19SD69,<timestamp>,Lisboa,32.737613,-15.303164
car,19SD79,<timestamp>,Lisboa,32.737613,-15.303164
car,19SD89,<timestamp>,Lisboa,32.737613,-15.303164
car,19SD99,<timestamp>,Lisboa,32.737613,-15.303164
```

**2.5.7. Teste do identificador " \* "**  
Este teste deve devolver todas as observações encontradas para o tipo de objeto em questão.
# A FAZER
Este teste podia devolver todos os objetos encontrados no sistema. Fica a dica ;)
# A FAZER

<br>

### **2.6. *trace***
Por fim, esta operação vai ser testada também no *spotter* utilizando o comando *trail* com um identificador.

**2.6.1. Teste com uma pessoa não avistada:**  
Este teste deve devolver vazio, visto que a pessoa não foi avistada por nenhuma das câmaras:

```
> trail person 14388236
```

**2.6.2. Teste com uma pessoa:**  
Este teste deve devolver uma lista de observações da pessoa em questão, ordenadas da mais recente para a mais antiga:

```
> trail person 123456789
person,123456789,<timestamp>,Alameda,30.303164,-10.737613
person,123456789,<timestamp>,Alameda,30.303164,-10.737613
person,123456789,<timestamp>,Tagus,38.737613,-9.303164
```

**2.6.3. Teste com um carro não avistado:**  
Este teste deve devolver vazio, visto que o carro não foi avistado por nenhuma das câmaras:

```
> trail car 12XD34
```

**2.6.4. Teste com um carro:**  
Este teste deve devolver uma lista de observações do carro em questão, ordenadas da mais recente para a mais antiga:

```
> trail car 00AA00
car,00AA00,<timestamp>,Tagus,38.737613,-9.303164
car,00AA00,<timestamp>,Tagus,38.737613,-9.303164
```



## **3. Replicação e Tolerância a Faltas**

Nesta nova secção, devem ser indicados os comandos para: lançar réplicas, fornecer dados, fazer interrogações, etc. Deve também ser indicado o que se espera que aconteça em cada caso.  
O guião de demonstração deve apresentar situações de funcionamento normal com replicação e também situações de tolerância a faltas.


## **4. Considerações Finais**

Dá-se por concluido o guia de demonstração que cobre todo o trabalho desenvolvido.

Apesar de não serem avaliados os comandos de controlo, o comando help deve ser minimamente informativo e deve indicar todas as operações existentes no spotter. Estes testes não cobrem tudo, pelo que devem ter sempre em conta os testes de integração e o código.
