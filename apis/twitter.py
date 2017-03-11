import tweepy

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
    twitter_username = ""
    twitter_message  = ""

    def __init__(self):
        return None


def send(msg):
    tweet = "@" + msg.twitter_username + " " + msg.twitter_message
    status = api.update_status(status=tweet)

def test():
    msg = TwitterMessage()
    # msg.create("evilpenguinsinspace", "Howdy", "Hello, World!")
    msg.twitter_username      = "AleenaCodes"
    msg.twitter_message = "WORK"
    send(msg)

test()
