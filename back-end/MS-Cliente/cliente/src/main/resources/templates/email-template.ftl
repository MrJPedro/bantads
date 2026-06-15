<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Notificação do Sistema</title>
</head>
<body style="margin:0; padding:0; background-color:#f4f6f8; font-family: Arial, Helvetica, sans-serif;">

<table width="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td align="center" style="padding: 30px 0;">

            <!-- Container -->
            <table width="600" cellpadding="0" cellspacing="0"
                   style="background-color:#ffffff; border-radius:8px; box-shadow:0 2px 8px rgba(0,0,0,0.08);">

                <!-- Header -->
                <tr>
                    <td style="padding: 20px 30px; background-color:#1f2933; border-radius:8px 8px 0 0;">
                        <h2 style="margin:0; color:#ffffff; font-size:20px;">
                            Bantads
                        </h2>
                    </td>
                </tr>

                <!-- Body -->
                <tr>
                    <td style="padding: 30px; color:#333333; font-size:14px; line-height:1.6;">
                        <p style="margin-top:0;">
                            Olá <strong>${nome}</strong>,
                        </p>

                        <p style="white-space: pre-line;">
                            ${mensagem}
                        </p>

                        <p>
                            Qualquer dúvida é só contatar o suporte pelo e-mail
                            <a href="mailto:${emailSuporte}" style="color:#2563eb; text-decoration:none;">
                                ${emailSuporte}
                            </a>.
                        </p>

                        <p style="margin-bottom:0;">
                            Att,<br>
                            <strong>Bantads</strong>
                        </p>
                    </td>
                </tr>

                <!-- Footer -->
                <tr>
                    <td style="padding: 15px 30px; background-color:#f1f5f9; border-radius:0 0 8px 8px;
                               color:#6b7280; font-size:12px; text-align:center;">
                        Este é um e-mail automático. Por favor, não responda.
                    </td>
                </tr>

            </table>

        </td>
    </tr>
</table>

</body>
</html>