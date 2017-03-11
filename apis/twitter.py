import tweepy

def get_api(cfg):
  auth = tweepy.OAuthHandler(cfg['consumer_key'], cfg['consumer_secret'])
  auth.set_access_token(cfg['access_token'], cfg['access_token_secret'])
  return tweepy.API(auth)

def main():
  # Fill in the values noted in previous step here
  cfg = {
    "consumer_key"        : "Fmtgapp73kEOmJ4iLCUajl75u",
    "consumer_secret"     : "IVBo9h27fHCgtbQv0046XHa0wAd0WJ9TZG95AY9avLZ1wPdPjx",
    "access_token"        : "840338539703336961-tLEq2UOwQYf1CzD3FdTPDAQX3vP3fqa",
    "access_token_secret" : "toWWXgIpqfxJEd1kTg2MYBBaE3ikRnHzFp92cclu9uwTC"
    }

  api = get_api(cfg)
  username = "@AleenaCodes"
  tweet = username + " " + "Do some work!"
  status = api.update_status(status=tweet)
  # Yes, tweet is called 'status' rather confusing

if __name__ == "__main__":
  main()
