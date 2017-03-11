import praw

reddit = praw.Reddit(client_id='***REMOVED***',
                     client_secret='***REMOVED***',
                     password='***REMOVED***',
                     username='BombScheduling',
                     user_agent='Sending messages (to people that want them) by /u/BombScheduling, StudentHackV project')

class RedditMessage():

    def __init__(self):
        return None

    def create(self):
        self.reddit_username      = ""

def send(details, msg):
    reddit.redditor(details.reddit_username).message(msg.message_title, msg.message_body)

# def test():
#     msg = RedditMessage()
#     # msg.create("***REMOVED***", "Howdy", "Hello, World!")
#     msg.reddit_username      = "***REMOVED***"
#     msg.reddit_message_title = "reddit_message_asofjtitle"
#     msg.reddit_message_body  = "reddit_message_body"
#     send(msg)
#
# test()
