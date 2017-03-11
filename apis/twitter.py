import tweepy
import re

cfg = {
    "consumer_key"        : "Fmtgapp73kEOmJ4iLCUajl75u",
    "consumer_secret"     : "IVBo9h27fHCgtbQv0046XHa0wAd0WJ9TZG95AY9avLZ1wPdPjx",
    "access_token"        : "840338539703336961-tLEq2UOwQYf1CzD3FdTPDAQX3vP3fqa",
    "access_token_secret" : "toWWXgIpqfxJEd1kTg2MYBBaE3ikRnHzFp92cclu9uwTC"
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
    p = re.compile(r'([A-Za-z0-9_]+)')
    return p.search(name)

# def test():
#     details = TwitterMessage()
#     # msg.create("evilpenguinsinspace", "Howdy", "Hello, World!")
#     details.twitter_username = "AleenaCodes"
#     msg  = message()
#     msg.message_body = "Hello!"
#     send(details, msg)
#
# test()
