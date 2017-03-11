from twilio.rest import TwilioRestClient

account_sid = "***REMOVED***" # Your Account SID from www.twilio.com/console
auth_token  = "***REMOVED***"  # Your Auth Token from www.twilio.com/console

twilnumber = "***REMOVED***"

client = TwilioRestClient(account_sid, auth_token)

class TextMessage():

    def __init__(self):
        return None

    def create(self):
        self.phone_number      = ""


def send(details, msg):
    return client.messages.create(body=msg.message_body, to=details.phone_number, from_=twilnumber)
    # print(message.sid)

def verify(details):
    return True


# message = client.messages.create(body="Hello from Python",
#     to="***REMOVED***",    # Replace with your phone number
#     from_="***REMOVED***") # Replace with your Twilio number


# class Message():
#     def __init__(self, message_body, message_title):
#         self.message_body = message_body
#         self.message_title = message_title

# def test():
#     phone = TextMessage()
#     phone.phone_number = "***REMOVED***"
#     msg = Message("Hello", "Not")
#     send(phone, msg)
#
# call = client.calls.create(url="http://demo.twilio.com/docs/voice.xml",
#     to="***REMOVED***",
#     send_digits="1234#",
#     from_=twilnumber,
#     method="GET")

# test()
