import smtplib
# from email.mime.text import MIMEText
# from email.mime.multipart import MIMEMultipart
import email.mime.text
import email.mime.multipart

fromaddr = "BombScheduling@gmail.com"

server = smtplib.SMTP('smtp.gmail.com', 587)
server.starttls()
server.login(fromaddr, "***REMOVED***")

# This requires enabling some google setting that allows "low access" apps
# or something to do things, it's probably okay for the time being.

class EmailMessage():

    def __init__(self):
        return None

    def create(self):
        self.email_address       = ""

def send(details, msg):
    message = email.mime.multipart.MIMEMultipart()
    message['From'] = fromaddr
    message['To'] = details.email_address
    message['Subject'] = msg.message_title
    body = msg.message_body
    message.attach(email.mime.text.MIMEText(body, 'plain'))
    text = message.as_string()
    server.sendmail(fromaddr, details.email_address, text)

def verify(details):
    return True

# server.quit()

# def test():
#     msg = EmailMessage()
#     msg.email_address       = "***REMOVED***"
#     msg.email_message_title = "no"
#     msg.email_message_body  = "So here's something that you can read."
#     send(msg)
#
# test()
