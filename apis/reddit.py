import praw

reddit = praw.Reddit(client_id='QNODIY8pI3SAHw',
                     client_secret='KgKRGwhIN1HUEpdRFSLtSPSIdu8',
                     password='cykablyat3',
                     username='BombScheduling',
                     user_agent='Sending messages (to people that want them) by /u/BombScheduling, StudentHackV project')

class RedditMessage():
    def __init__(self, reddit_username, reddit_message_title, reddit_message_body):
        self.reddit_username      = reddit_username
        self.reddit_message_title = reddit_message_title
        self.reddit_message_body  = reddit_message_body

def send(msg):
    reddit.redditor(msg.reddit_username).message(msg.reddit_message_title, msg.reddit_message_body)

# def test():
#     msg = RedditMessage("evilpenguinsinspace", "Howdy", "Hello, World!")
#     send(msg)
#
# test()
