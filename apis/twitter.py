import tweepy
import re

cfg = {
    "consumer_key"        : "***REMOVED***",
    "consumer_secret"     : "***REMOVED***",
    "access_token"        : "***REMOVED***",
    "access_token_secret" : "***REMOVED***"
}

auth = tweepy.OAuthHandler(cfg['consumer_key'], cfg['consumer_secret'])
auth.set_access_token(cfg['access_token'], cfg['access_token_secret'])
api = tweepy.API(auth)

class TwitterMessage():

    def __init__(self):
        return None

    def create(self):
        self.twitter_username = ""

def send(details, msg):
    tweet = "@" + details.twitter_username + " " + msg.message_body
    try:
        status = api.update_status(status=tweet)
    except tweepy.error.TweepError:
        print("Failed to tweet due to duplicate message")

def verify(details):
    name = details.twitter_username
    if name == "":
        return True
    p = re.compile(r'([A-Za-z0-9_]+)')
    return p.search(name)

# def test():
#     details = TwitterMessage()
#     # msg.create("***REMOVED***", "Howdy", "Hello, World!")
#     details.twitter_username = "***REMOVED***"
#     msg  = message()
#     msg.message_body = "Hello!"
#     send(details, msg)
#
# test()
