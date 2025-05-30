package com.example.demo;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class AutomacaoPonto implements CommandLineRunner {

    private final AppConfig appConfig;

    @Autowired
    public AutomacaoPonto(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    private WebDriver driver;
    private WebDriverWait wait;

    @Override
    public void run(String... args) {
        // Caminho do perfil do Chrome
        ChromeOptions options = new ChromeOptions();

        // Abre o navegador em modo anônimo
        options.addArguments("--incognito");
        options.addArguments("--start-maximized");

        // Caminho do executável do Chrome
        options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Espera explícita de até 10 segundos

        try {
            realizarLogin();
            acessarMenuFrequencia();
            abrirModalRequerimento();

            // Aguarda 5 segundos antes de finalizar
            Thread.sleep(5000);

            System.out.println("Programa finalizado com sucesso!");

        } catch (InterruptedException e) {
            System.out.println("Erro no tempo de espera: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Elemento não encontrado: " + e.getMessage());
        } catch (WebDriverException e) {
            System.out.println("Erro no WebDriver: " + e.getMessage());
        } finally {
            driver.quit();
            System.out.println("Driver encerrado com sucesso!");
        }
    }

    /**
     * Realiza o login na aplicação.
     */
    private void realizarLogin() {
        driver.get("https://castgroup.izeus.com.br/ponto");
        System.out.println("Página de login acessada com sucesso!");

        WebElement userInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usuid")));
        userInput.sendKeys(appConfig.getNome());

        WebElement passwordInput = driver.findElement(By.id("ususenha"));
        passwordInput.sendKeys(appConfig.getSenha());

        WebElement loginButton = driver.findElement(By.id("btEntrar"));
        loginButton.click();

        System.out.println("Login realizado com sucesso!");
        wait.until(ExpectedConditions.urlContains("ponto"));
    }

    /**
     * Acessa o menu de frequência.
     */
    private void acessarMenuFrequencia() throws InterruptedException {
        System.out.println("Acessando o menu de frequência...");

        // Aguarda até que a div com o id 'cssmenuatalho' esteja visível
        WebElement menuAtalhoDiv = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("cssmenuatalho"))
        );

        // Localiza todas as <li> dentro dessa div
        List<WebElement> menuItems = menuAtalhoDiv.findElements(By.tagName("li"));

        // Procura a primeira <li> com a classe 'last'
        for (WebElement item : menuItems) {
            String classAttribute = item.getAttribute("class");
            if (classAttribute != null && classAttribute.contains("last")) {
                WebElement menuLink = item.findElement(By.tagName("a"));
                menuLink.click();
                System.out.println("Menu de frequência acessado com sucesso!");

                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ol.breadcrumb")));

                WebElement breadcrumb = driver.findElement(By.cssSelector("ol.breadcrumb"));
                String breadcrumbText = breadcrumb.getText();

                if (breadcrumbText.contains("FREQUÊNCIA")) {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tbDemonstrativo")));
                    System.out.println("Página de frequência carregada com sucesso!");
                } else {
                    throw new NoSuchElementException("Texto 'FREQUÊNCIA' não encontrado no breadcrumb!");
                }
                return;
            }
        }

        System.out.println("Erro: Nenhum menu de frequência foi encontrado!");
    }

    private void abrirModalRequerimento() {
        System.out.println("Abrindo o modal de requerimento...");

        List<WebElement> dangerRows = driver.findElements(By.xpath("//*[@id='tbDemonstrativo']/tbody/tr"));

        for (int i = 1; i <= dangerRows.size(); i++) {
            WebElement row = driver.findElement(By.xpath("//*[@id='tbDemonstrativo']/tbody/tr[" + i + "]"));
            String rowClass = row.getAttribute("class");

            if (rowClass != null && rowClass.contains("danger")) {
                WebElement column7 = driver.findElement(By.xpath("//*[@id='tbDemonstrativo']/tbody/tr[" + i + "]/td[7]"));
                String columnHtml = column7.getAttribute("innerHTML").trim();

                // Verifica se a coluna contém apenas o texto "Falta" (sem HTML adicional)
                if (columnHtml.equals("Falta")) {
                    WebElement button = driver.findElement(By.xpath("//*[@id='tbDemonstrativo']/tbody/tr[" + i + "]/td[1]/button"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);

                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalAddRequerimento")));
                    WebElement modalTitle = driver.findElement(By.cssSelector("#modalAddRequerimento h4.modal-title"));
                    if (modalTitle.getText().equals("Requerimento")) {
                        System.out.println("Modal de requerimento aberto com sucesso!");
                        preencherRequerimento();
                    } else {
                        throw new NoSuchElementException("Título 'Requerimento' não encontrado no modal!");
                    }
                } else {
                    System.out.println("Linha " + i + " já possui informações adicionais na coluna 7. Pulando para a próxima linha.");
                }
            }
        }
    }


    /**
     * Preenche o modal de requerimento selecionando a opção correta.
     */
    private void preencherRequerimento() {
        try {
            WebElement dropdownButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='campo']/div[1]/div/div/div/button"))
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", dropdownButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdownButton);
            System.out.println("Botão do dropdown clicado com sucesso!");

            WebElement itemInserirMarcacao = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//ul[contains(@class, 'dropdown-menu inner')]//span[text()='INSERIR MARCAÇÃO']/ancestor::a")
                    )
            );
            itemInserirMarcacao.click();
            System.out.println("Opção 'INSERIR MARCAÇÃO' selecionada diretamente!");

            // Aguarda o título carregar
            wait.until(ExpectedConditions.textToBe(By.xpath("//*[@id='titulo']"), "INSERIR MARCAÇÃO"));
            System.out.println("Título 'INSERIR MARCAÇÃO' carregado com sucesso!");

            // Adiciona marcações
            WebElement addButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@onclick, 'addInserirMarcData')]"))
            );
            for (int i = 0; i < 3; i++) {
                addButton.click();
                Thread.sleep(500);
            }
            System.out.println("Três campos de marcação adicionados com sucesso!");

            Actions actions = new Actions(driver);

            // Preenche os campos de marcação usando elementos WebElement
            WebElement input1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='addMarcacao']/div/div[1]/input[1]")));
            input1.click();
            actions.keyDown(Keys.CONTROL).sendKeys(Keys.ARROW_LEFT).keyUp(Keys.CONTROL).perform();
            input1.sendKeys("0800");

            WebElement input2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='addMarcacao']/div/div[1]/input[2]")));
            input2.click();
            actions.keyDown(Keys.CONTROL).sendKeys(Keys.ARROW_LEFT).keyUp(Keys.CONTROL).perform();
            input2.sendKeys("1200");

            WebElement input3 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='addMarcacao']/div/div[1]/input[3]")));
            input3.click();
            actions.keyDown(Keys.CONTROL).sendKeys(Keys.ARROW_LEFT).keyUp(Keys.CONTROL).perform();
            input3.sendKeys("1300");

            WebElement input4 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='addMarcacao']/div/div[1]/input[4]")));
            input4.click();
            actions.keyDown(Keys.CONTROL).sendKeys(Keys.ARROW_LEFT).keyUp(Keys.CONTROL).perform();
            input4.sendKeys("1700");

            System.out.println("Campos de marcação preenchidos com sucesso!");
            Thread.sleep(500);

            // Clica no botão Enviar
            WebElement enviarButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='btnEnviar']"))
            );
            enviarButton.click();
            System.out.println("Botão Enviar clicado com sucesso!");

            // Aguarda o carregamento da mensagem de sucesso
            WebElement successMessage = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[@class='bootbox-body' and text()='Requerimento enviado com sucesso.']")
                    )
            );
            System.out.println("Mensagem de sucesso exibida: " + successMessage.getText());

            // Clica no botão OK na modal de sucesso
            WebElement okButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[17]/div/div/div[2]/button"))
            );
            okButton.click();
            System.out.println("Botão OK clicado com sucesso!");

            Thread.sleep(500);
        } catch (TimeoutException e) {
            System.out.println("Erro: Tempo esgotado ao tentar clicar no botão dropdown! " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Erro: Botão dropdown ou item não encontrado! " + e.getMessage());
        } catch (ElementClickInterceptedException e) {
            System.out.println("Erro: Clique no botão dropdown foi interceptado! Tentando novamente com JavaScript...");
            WebElement dropdownButton = driver.findElement(By.xpath("//*[@id='campo']/div[1]/div/div/div/button"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdownButton);
        } catch (InterruptedException e) {
            System.out.println("Erro no tempo de espera: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
        }
    }


}
