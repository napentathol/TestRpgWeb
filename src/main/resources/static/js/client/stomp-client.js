/**
 * Created by Alex on 2/3/2015.
 */

if(angular.isUndefined(rpg)) {
    var rpg = {
        $ng : angular.module('rpg', [])
    };
}

(function() {
    var maxMessages = 150;

    var StompClient = function() {
        var connected = false;
        var stompClient = null;
        var messageHandlers = [];
        var that = this;

        this.isConnected = function() {
            return connected;
        };

        this.connect = function () {
            var socket = new SockJS('/hello');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function(frame) {
                connected = true;
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/greetings', function(greeting){
                    that.showGreeting(JSON.parse(greeting.body));
                });
            });
        };

        this.disconnect = function() {
            stompClient.disconnect();
            connected = false;

            console.log("Disconnected");
        };

        this.sendMessage = function(name, message) {
            var msg = {
                'username': name,
                'payload': message
            };

            console.log(msg);

            stompClient.send(
                '/app/message',
                {},
                JSON.stringify(msg)
            );
        };

        this.showGreeting = function(message) {
            console.log(message);

            for(var i in messageHandlers){
                messageHandlers[i](message);
            }
        };

        this.addMessageHandler = function handler(handler) {
            if(typeof handler === 'function'
                    && handler.length === 1) {
                messageHandlers.push(handler);
            }
        };

        this.connect();
    };

    var MessageDirective = function(StompClient) {
        return ({
            controller : function($scope) {
                $scope.sendMessage = function() {
                    StompClient.sendMessage($scope.name, $scope.msg);
                }
            },
            restrict : 'E',
            templateUrl : 'partials/message.html'
        });
    };

    var WellDirective = function(StompClient) {
        return ({
            controller : function($scope) {
                $scope.messages = [];

                function handler(message) {
                    $scope.messages.push(message);

                    if($scope.messages.length > maxMessages) {
                        $scope.messages.shift();
                        $scope.messages.shift();
                    }

                    $scope.$apply();
                }

                StompClient.addMessageHandler(handler);
            },
            restrict : 'E',
            templateUrl : 'partials/well.html'
        })
    };

    rpg.$ng.service('StompClient', StompClient);

    rpg.$ng.directive('messagingInput', ['StompClient', MessageDirective]);
    rpg.$ng.directive('messagingWell', ['StompClient', WellDirective]);
})();
