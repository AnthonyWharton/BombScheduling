from twilio.rest import TwilioRestClient

account_sid = "ACa27af77533c4f5198ce9706e589ff4ff" # Your Account SID from www.twilio.com/console
auth_token  = "5e6ec2c5fe46ac0f3b2535a416fd3034"  # Your Auth Token from www.twilio.com/console

twilnumber = "+447481342662"

client = TwilioRestClient(account_sid, auth_token)

class TextMessage():

    def __init__(self):
        return None

    def create(self):
        self.phone_number      = ""


def send(details, msg):
    return client.message.create(body=msg.message_body, to=details.phone_number, from_=twilnumber)

    # print(message.sid)


# message = client.messages.create(body="Hello from Python",
#     to="+447716451341",    # Replace with your phone number
#     from_="+447481342662") # Replace with your Twilio number


# class Message():
#     def __init__(self, message_body, message_title):
#         self.message_body = message_body
#         self.message_title = message_title

# def test():
#     phone = TextMessage()
#     phone.phone_number = "+447716451341"
#     msg = Message("Hello", "Not")
#     send(phone, msg)
#
# call = client.calls.create(url="http://demo.twilio.com/docs/voice.xml",
#     to="+447716451341",
#     send_digits="1234#",
#     from_=twilnumber,
#     method="GET")

# test()
