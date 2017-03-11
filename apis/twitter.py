import tweepy

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

def send(msg):
    tweet = "@" + msg.twitter_username + " " + msg.twitter_message
    status = api.update_status(status=tweet)

# def test():
#     msg = TwitterMessage()
#     # msg.create("***REMOVED***", "Howdy", "Hello, World!")
#     msg.twitter_username = "***REMOVED***"
#     msg.twitter_message  = "WORK"
#     send(msg)
#
# test()
