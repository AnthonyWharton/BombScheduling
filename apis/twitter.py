import tweepy

def get_api(cfg):
  auth = tweepy.OAuthHandler(cfg['consumer_key'], cfg['consumer_secret'])
  auth.set_access_token(cfg['access_token'], cfg['access_token_secret'])
  return tweepy.API(auth)

def main():
  # Fill in the values noted in previous step here
  cfg = {
    "consumer_key"        : "***REMOVED***",
    "consumer_secret"     : "***REMOVED***",
    "access_token"        : "***REMOVED***",
    "access_token_secret" : "***REMOVED***"
    }

  api = get_api(cfg)
  username = "@***REMOVED***"
  tweet = username + " " + "Do some work!"
  status = api.update_status(status=tweet)
  # Yes, tweet is called 'status' rather confusing

if __name__ == "__main__":
  main()
