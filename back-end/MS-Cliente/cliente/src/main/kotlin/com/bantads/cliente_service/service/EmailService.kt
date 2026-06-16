package com.bantads.cliente_service.service

import com.bantads.cliente_service.dto.DadosClienteResponse
import com.bantads.cliente_service.entity.TipoEmail
import freemarker.template.Configuration
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils

@Service
class EmailService(
    private val fmConfiguration: Configuration,
    private val emailSender: JavaMailSender,
    @Value("\${spring.mail.username}")
    private val email_suporte: String
) {

    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    fun notificarClienteEmail(tipo: TipoEmail, email: String, nome: String, atributo: String) {
        val atributoSeguro = escapeHtml(atributo)
        val dados = mutableMapOf<String, Any>(
            "nome" to nome,
            "emailSuporte" to email_suporte
        )

        val assunto = when (tipo) {
            TipoEmail.ERRO -> {
                dados["mensagem"] = "Ocorreu um erro ao realizar seu cadastro no Bantads. Verifique seus dados e tente novamente."
                "Cadastro com erro"
            }
            TipoEmail.APROVACAO -> {
                dados["mensagem"] = "Estamos felizes em ter você em nosso sistema :)<br><br>" +
                        "Seu cadastro foi realizado com sucesso, sua senha é ${atributo}."
                "Cadastro aprovado"
            }
            TipoEmail.REJEICAO -> {
                dados["mensagem"] = "Seu cadastro foi rejeitado no sistema Bantads :(\n\n" +
                        "Motivo informado: $atributoSeguro"
                "Cadastro rejeitado"
            }
        }

        sendEmail(email, assunto, dados)
    }

    private fun escapeHtml(valor: String): String {
        return valor
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    private fun sendEmail(
        destinatario: String,
        assunto: String,
        dados: Map<String, Any>
    ) {
        try {
            val mimeMessage = emailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

            helper.setFrom(email_suporte)
            helper.setTo(destinatario)
            helper.setSubject(assunto)

            val template = fmConfiguration.getTemplate("email-template.ftl")
            val html = FreeMarkerTemplateUtils.processTemplateIntoString(template, dados)

            helper.setText(html, true)

            emailSender.send(mimeMessage)
            logger.info("E-mail enviado com sucesso para: {}", destinatario)

        } catch (e: Exception) {
            logger.error("Erro ao enviar e-mail para: {}", destinatario, e)
        }
    }
}
