/**
 * Created by Alex on 2/3/2015.
 */

if(angular.isUndefined(rpg)) {
    var rpg = {
        $ng : angular.module('rpg', [])
    };
}

(function() {
    // Max messages possible.
    var maxMessages = 150;

    /**
     * Stomp Client Service.
     *
     * @constructor - creates a Stomp Client Service.
     */
    var StompClient = function() {
        var connected = false;
        var stompClient = null;
        var messageHandlers = [];
        var lineHandlers = [];
        var that = this;

        /**
         * Return if the client is connected.
         *
         * @returns {boolean} if it is connected.
         */
        this.isConnected = function() {
            return connected;
        };

        /**
         * Connect to the websocket server.
         */
        this.connect = function () {
            var socket = new SockJS(window.location.pathname + 'hello');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function(frame) {
                connected = true;
                console.log('Connected: ' + frame);

                stompClient.subscribe('/topic/message', function(greeting){
                    that.showGreeting(JSON.parse(greeting.body));
                });

                stompClient.subscribe('/topic/draw', function(line){
                    that.showLine(JSON.parse(line.body));
                });
            });
        };

        /**
         * Disconnect from the websocket server.
         */
        this.disconnect = function() {
            stompClient.disconnect();
            connected = false;

            console.log("Disconnected");
        };

        /**
         * Send a message to the server.
         *
         * @param name - the user's name.
         * @param message - the returned message
         */
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

        /**
         * Sends a die to be rolled.
         *
         * @param name - username.
         * @param num - number of dice.
         * @param val - die value.
         * @param add - number to add.
         */
        this.sendDie = function(name, num, val, add) {
            var roll = {
                'username' : name,
                'num' : num,
                'val' : val,
                'add' : add
            };

            console.log(roll);

            stompClient.send(
                '/app/roll',
                {},
                JSON.stringify(roll)
            );
        };

        /**
         * Sends a line.
         *
         * @param x - point 1 x.
         * @param y - point 1 y.
         * @param nx - point 2 x.
         * @param ny - point 2 y.
         * @param color - the color to print with.
         */
        this.sendLine = function(x, y, nx, ny, color) {
            var line = {
                'x' : x,
                'y' : y,
                'nx' : nx,
                'ny' : ny,
                'color' : color
            };

            stompClient.send(
                '/app/draw',
                {},
                JSON.stringify(line)
            );
        };

        /**
         * Shows a message.
         *
         * @param message - the message to log.
         */
        this.showGreeting = function(message) {
            for(var i in messageHandlers){
                messageHandlers[i](message);
            }
        };

        /**
         * Shows a line.
         *
         * @param line - the message to log.
         */
        this.showLine = function(line) {
            for(var i in lineHandlers){
                lineHandlers[i](line);
            }
        };

        /**
         * Adds a message handler.
         *
         * @param handler - message handler.
         */
        this.addMessageHandler = function handler(handler) {
            if(typeof handler === 'function'
                && handler.length === 1) {
                messageHandlers.push(handler);
            }
        };

        /**
         * Adds a line handler.
         *
         * @param handler - line handler.
         */
        this.addLineHandler = function handler(handler) {
            if(typeof handler === 'function'
                    && handler.length === 1) {
                lineHandlers.push(handler);
            }
        };

        this.connect();
    };

    /**
     * Navbar directive.
     *
     * @returns {{restrict: string, templateUrl: string}} - directive.
     * @constructor - constructs a Navbar Directive.
     */
    var NavbarDirective = function() {
        return ({
            restrict : 'E',
            templateUrl : 'partials/navbar.html'
        })
    };

    /**
     * Message directive.
     *
     * @param StompClient - Stomp Client service.
     * @returns {{controller: Function, restrict: string, templateUrl: string}} - directive.
     * @constructor - constructs a Message Directive.
     */
    var MessageDirective = function(StompClient) {
        return ({
            controller : function($scope) {
                $scope.sendMessage = function() {
                    StompClient.sendMessage($scope.name, $scope.msg);
                };

                $scope.drawActive = false;
                $scope.inputActive = true;

                $scope.showDraw = function() {
                    $scope.drawActive = true;
                    $scope.inputActive = false;
                };

                $scope.showInput = function() {
                    $scope.drawActive = false;
                    $scope.inputActive = true;
                };
            },
            restrict : 'E',
            templateUrl : 'partials/message.html'
        });
    };

    /**
     * Dice directive.
     *
     * @param StompClient - Stomp Client Service.
     * @returns {{controller: Function, restrict: string, templateUrl: string}} - directive.
     * @constructor - constructs a Dice Directive.
     */
    var DiceDirective = function(StompClient) {
        return ({
            controller : function($scope) {
                $scope.sendDie = function() {
                    StompClient.sendDie($scope.name, $scope.num, $scope.val, $scope.add);
                }
            },
            restrict : 'E',
            templateUrl : 'partials/dice.html'
        });
    };

    /**
     * Drawing Canvas Directive.
     *
     * @param StompClient - Stomp Client Service.
     * @returns {{controller: Function, restrict: string, templateUrl: string}}
     * @constructor - constructs a drawing canvas.
     */
    var DrawDirective = function(StompClient) {
        var prev = {
            x:0,
            y:0
        };

        return ({
            controller : function($scope) {
                $scope.sendLine = function(event) {
                    StompClient.sendLine(
                        event.offsetX,
                        event.offsetY,
                        0
                    )
                };

                $scope.isDrawing = false;
            },
            restrict : 'E',
            templateUrl : 'partials/draw.html',
            link : function(scope, element, attr) {
                var canvas = element.children(0);

                var time = $.now();

                console.log(canvas);

                canvas.bind('mousedown', function(e){
                    scope.isDrawing = true;

                    prev.x = e.offsetX;
                    prev.y = e.offsetY;
                });

                canvas.bind('mouseup mouseleave', function(e){
                    scope.isDrawing = false;
                });

                canvas.bind('mousemove', function(e) {
                    if(scope.isDrawing && $.now() - time > 30) {
                        StompClient.sendLine(
                            e.offsetX,
                            e.offsetY,
                            prev.x,
                            prev.y,
                            0
                        );

                        prev.x = e.offsetX;
                        prev.y = e.offsetY;

                        time = $.now();
                    }
                });
                var ctx = canvas[0].getContext('2d');

                StompClient.addLineHandler(function(line){
                    ctx.moveTo(line.x, line.y);
                    ctx.lineTo(line.nx, line.ny);
                    ctx.stroke();
                })
            }
        });
    };

    /**
     * Input Manager directive.
     *
     * @returns {{controller: Function, restrict: string, templateUrl: string}} - directive.
     * @constructor - constructs an input manager.
     */
    var InputManagerDirective = function() {
        return ({
            controller : function($scope) {
                if(!angular.isDefined($scope.dropdown)) {
                    $scope.dropdown = {
                        input : {}
                    }
                }

                $scope.name = "GenericUser";
                $scope.dropdown.input.choice = "Roll!";

                $scope.rollActive = true;
                $scope.messageActive = false;

                $scope.showRoll = function() {
                    $scope.rollActive = true;
                    $scope.messageActive = false;

                    $scope.dropdown.input.choice = "Roll!";
                };

                $scope.showMessage = function() {
                    $scope.rollActive = false;
                    $scope.messageActive = true;

                    $scope.dropdown.input.choice = "Message!";
                };
            },
            restrict : 'E',
            templateUrl : 'partials/input.html'
        });
    };

    /**
     * Well Directive.
     *
     * @param StompClient - Stomp Client service.
     * @param $http - http service.
     * @returns {{controller: Function, restrict: string, templateUrl: string}} - directive.
     * @constructor - constructs a Well Directive.
     */
    var WellDirective = function(StompClient, $http) {
        return ({
            controller : function($scope) {
                $scope.messages = [];

                $http.get("message/list")
                .success(function(data, status, headers, config){
                    $scope.messages = data;
                }).error(function(data, status, headers, config){
                    console.log("Error occurred: " + data);
                });

                function handler(message) {
                    $scope.messages.push(message);

                    if($scope.messages.length > maxMessages) {
                        $scope.messages.shift();
                        $scope.messages.shift();
                    }
                    $scope.$apply();

                    var wells = $('.chat-well');
                    wells[0].scrollTop = wells[0].scrollHeight;
                }

                StompClient.addMessageHandler(handler);
            },
            restrict : 'E',
            templateUrl : 'partials/well.html'
        })
    };

    /**
     * Stop Event Directive.
     *
     * @returns {{restrict: string, link: Function}} - directive.
     * @constructor - constructs a Stop Event Directive.
     */
    var StopEventDirective = function() {
        return ({
            restrict: 'A',
            link: function(scope, element, attr) {
                element.bind('click', function(e){
                    e.stopPropagation();
                });
            }
        })
    };

    rpg.$ng.service('StompClient', StompClient);

    rpg.$ng.directive('navbar', [NavbarDirective]);
    rpg.$ng.directive('inputManager', [InputManagerDirective]);
    rpg.$ng.directive('messagingInput', ['StompClient', MessageDirective]);
    rpg.$ng.directive('diceInput', ['StompClient', DiceDirective]);
    rpg.$ng.directive('messagingWell', ['StompClient', '$http', WellDirective]);
    rpg.$ng.directive('drawCanvas', ['StompClient', DrawDirective]);
    rpg.$ng.directive('stopEvent', [StopEventDirective]);
})();
