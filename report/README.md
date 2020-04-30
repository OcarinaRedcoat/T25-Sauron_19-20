![IST Lgo](IST_C_RGB_POS.png "IST Logo")

# **Relatório do Projeto Sauron**

Sistemas Distribuídos<br>2019-2020

## **Autores** 
### **Grupo T25** 

<br>

| Number | Name              | User                                 | Email                                              |
|--------|-------------------|--------------------------------------|----------------------------------------------------|
| 84756  | Pedro Teixeira    | <https://github.com/NikonPT>         | <mailto:pedro.r.teixeria@tecnico.ulisboa.pt>       |
| 87636  | Bernardo Faria    | <https://github.com/BernardoFaria>   | <mailto:bernardo.faria@tecnico.ulisboa.pt>         |
| 87699  | Ricardo Caetano   | <https://github.com/OcarinaRedcoat>  | <mailto:ricardo.caetano.aleixo@tecnico.ulisboa.pt> |

<br>

*(usar imagens com 150px de altura; e depois apagar esta linha)*  
![Pedro](alice.png) ![Bernardo](87636.jpg) ![Ricardo](87699.png)

<br>

## **Melhorias da primeira parte**
_(que correções ou mehroias foram feitas ao código da primeira parte -- incluir link para commits no GitHub onde  as altearções foram feitas)

**EYE**
* argumentos da linha de comando corretos: fizemos uma correção nesta medida, tirando o primeiro argumento que por lapso estava errado;
* envio de lote de observações: corrigimos o nosso código, que não permitia a diferença de lotes; cada observação era enviada diretamente e não após uma linha vazia.

**SILO**
* Adicionamos o guião de demonstração que estava em falta na primeira entrega;
* Corrigimos o tratamento de erros (mapeamento de erros para gRPC).

**SPOTTER**
* argumentos da linha de comandos corretos: fizemos também uma correção nesta medida, tirando o primeiro argumento que por lapso estava errado. 

**SILO-CLIENT**
* Cobertura de testes de integração desenvolvidos: adicionamos os testes para cada uma das seguintes operações:
    * cam_join;
    * cam_info;
    * report;
    * track;
    * track_match.

**QUALIDADE DO CÓDIGO**  
* Sincronização correta de variáveis partilhadas: foi adicionada esta medida ao nosso código.



<br>

## **Modelo de faltas**

_(que faltas são toleradas, que faltas não são toleradas)_

<br>

## **Solução**

_(Figura da solução de tolerância a faltas)_

_(Breve explicação da solução, suportada pela figura anterior)_

<br>

## **Protocolo de replicação**

Esta secção desdobra-se sobre o protocolo de replicação utilizado no desenvolvimento do projeto, contendo a sua explicação bem como a descrição das trocas de mensagens. As modificações feitas por forma a garantir a coerência com o que nos foi pedido é apresentada na última secção deste relatório, referente às **<i>Opções De Implementação.</i>**

Para a resolução do trabalho, o nosso grupo implementou o protocolo gossip architecture com coerência fraca, que passamos já a explicar.  

Este protocolo proveio de uma proposta académica de 1992, que desenvolveu o que chamamos de *gossip architecture* como uma framework para implementar serviços altamente disponiveis que replicam os dados perto de pontos em que grupos de clientes precisam deles. O nome deriva do facto de os *replica managers* trocarem mensagens de “gossip” periodicamente para transmitir as atualizações que cada uma delas recebeu de clientes, em background (como se espalhassem um boato ou um rumor). Oferecer sempre acesso rápido aos clientes, mesmo em situações de partições, sacrificando a coerência, são os objetivos principais deste protocolo.

De uma forma muito reduzida, podemos explicar o seu funcionamento em duas frases sucintas:  
* Os clientes enviam pedidos de leitura (*queries*) ou de *update* a uma réplica próxima;  
* As réplicas progagam os *updates* de forma relaxada, podendo ter vistas divergentes.   

Este sistema fornece duas garantias, mesmo que as *replica managers* possam não estar a comunicar nesse exato momento:
* Mesmo que um cliente aceda a diferentes réplicas, os valores que lê são coerentes entre si (um cliente nunca lê um valor recente e depois um valor mais antigo);
* Consistẽncia entre réplicas: todas as *replicas managers* recebem eventualmente todos os updates e aplicam-nos tendo em conta a **ordem causal** entre os mesmos (por exemplo, se um update u2 depende de outro u1, a *replica manager* nunca vai executar u2 sem antes ter executado u1).

Passamos a explicar o algoritmo da interação cliente-réplica:
* Cada cliente vai manter um *timestamp vetorial* chamado **prevTS**; este consiste num vetor de inteiros, onde cada entrada representa cada réplica, onde se reflete a última versão acedida pelo cliente;
* Em cada pedido a uma réplica, o cliente vai enviar (*pedido*, **prevTS**);
* A réplica em casa responde com (*resposta*, **newTS**), onde **newTS** é o *timestamp vetorial* que reflete o estado da réplica;
* O cliente, por fim, atualiza o seu **prevTS** com **newTS**: para cada entrada **i** do vetor, atualiza **prevTS[i]** se **newTS[i] > prevTS[i]**.

Uma *replica manager* tem um estado com vários objetos incluídos:
* Um *replica timestamp*, que reflete os *updates* no *Update Log*;
* Um *Update Log*, que consiste numa lista de *updates* recebidos pelo front end (faz sentido porque uma réplica pode já ter recebido um update mas não o pode executar porque falta receber/executar dependências causais, e permite prograpar *updates* individuais às restantes réplicas; 
* Um *Value Tmestamp* que reflete os *updates* executados localmente;
* Um *Value*, ou seja, o estado replicado.

Por fim, vamos expor a forma como acontece a troca de mensagens, atendendo aos pedidos de leitura, aos pedidos de *update*, e à propagação de *updates*. 

**Pedidos de leitura**:
* Nestes, a réplica verifica se **pedido.prevTS <= valueTS**. Se sim, retorna o valor atual juntamente com o **valueTS**. Se não, o pedido fica pendente.

**Pedidos de update**: Quando a réplica *i* recebe o *update* do cliente, verifica se já o executou. Se sim, descarta-o. Caso contrário:  
* Incrementa a entrada *i* do seu *replica timestamp* em uma unidade;  
* Atribui ao *update* um novo timestamp;  
* Junta o *update* ao *log* e retorna o novo *timestamp* ao cliente;  
* Espera até que **pedido.prevTS <= valueTS** se verifique para executar o pedido localmente, garantindo a **ordem causal**;  
* Assim que executar o pedido, atualiza o *value timestamp*: para cada entrada *i*, atualiza **valueTS[i]** se **replicaTS[i] > valueTS[i]**.      

**Propagação de updates**:       
* Periodicamente, cada réplica *i* contacta outra réplica *j*;  
* *i* envia a *j* os *updates* do *log* de *i* que *j* não tem, pela ordem certa;  
* Para cada *update*:  
    * Se não for duplicado, acrescenta-o ao seu *log*;  
    * Atualiza o seu **replicaTS**;  
    * Assim que **prevTS <= valueTS**, executa o *update*.  

<br>

## **Opções de implementação**

_(Descrição de opções de implementação, incluindo otimizações e melhorias introduzidas)_

<br>

## **Notas finais**

Damos por concluído este relatório referente à 2ª entrega do projeto de Sistemas Distribuídos.