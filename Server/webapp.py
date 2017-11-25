import json
import time
from flask import Flask, jsonify, make_response, request, abort

app = Flask(__name__)

# transactions = []


def read_list():
    try:
        with open(LIST_FILENAME, 'r') as json_file:
            trans = json.load(json_file)
    except IOError:
        print('No list found, creating a new one')
        trans = []
    return trans


def write_list():
    with open(LIST_FILENAME, 'w') as json_file:
        json.dump(transactions, json_file)


@app.route('/list', methods=['GET'])
def get_list():
    global transactions
    transactions = read_list()
    now = time.strftime("%Y%m%d %H%M%S")
    print("%s: GET" % (now))
    return jsonify(transactions)


@app.route('/update', methods=['POST'])
def update_list():
    if not request.json:
        abort(400)
    now = time.strftime("%Y%m%d %H%M%S")
    print("%s: POST" % (now))
    global transactions
    transactions = request.json
    write_list()
    return make_response(jsonify({'Updated': True}), 200)


@app.errorhandler(405)
def bad_method(error):
    return make_response(jsonify({'error': 'Bad method'}), 405)

@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not found'}), 404)


@app.errorhandler(400)
def bad_request(error):
    return make_response(jsonify({'error': 'Bad request'}), 400)

LIST_FILENAME = 'gnubalance.json'
transactions = read_list()

if __name__ == '__main__':
    #global transactions
    #transactions = read_list()
    app.run(host='0.0.0.0')
