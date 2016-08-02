function getDomXY(a) {
    for (var b = a.offsetLeft,
             c = a.offsetTop,
             d = a.offsetParent; null !== d;) b += d.offsetLeft,
        c += d.offsetTop,
        d = d.offsetParent;
    return {
        x: b,
        y: c
    }
}

var Gomoku = {
    createNew : function(canvas){
        var gmk = {};

        gmk.canvas = canvas;
        gmk.ct = canvas.getContext("2d");

        //用来记录棋子的栈
        gmk.steps = Stack.createNew();

        //棋子的数据
        gmk.chessData = new Array(15);

        //允许下棋
        gmk.allowPlay = false;

        //我的回合
        gmk.myTurn = false;

        gmk.whiteChessBrush = ChessBrush.createNew(gmk.ct, "images/w.png");
        gmk.blackChessBrush = ChessBrush.createNew(gmk.ct, "images/b.png");

        //棋盘刷子
        gmk.boardBrush = BoardBrush.createNew(gmk.ct);


        gmk.init = function(){
            for (var x = 0; x < 15; ++x){
                gmk.chessData[x] = new Array(15);
                for (var y = 0; y < 15; ++y){
                    gmk.chessData[x][y] = 0;
                }
            }
            gmk.boardBrush.show();
        }

        gmk.init();

        gmk.getAbsoluteX = function(x){
            var pos = getDomXY(canvas);
            var l = document.body.scrollLeft;
            return parseInt((x - pos.x + l - 20) / 40);
        }

        gmk.getAbsoluteY = function(y){
            var pos = getDomXY(canvas);
            var h = document.body.scrollTop;
            return parseInt((y - pos.y + h - 20) / 40);
        }

        gmk.play = function(e){
            //alert("play hahaha");
            var x = gmk.getAbsoluteX(e.clientX);
            var y = gmk.getAbsoluteY(e.clientY);
            if (gmk.chessData[x][y] != 0){
                return [false, "你不能在这个位置下棋"];
            }
            gmk.whiteChessBrush.drawChess(x, y);
        }

        gmk.hanlderEvent = function(result){
            if (result.success == false){
                alert("系统异常" + event.data);
            }
            var action = result.model;
            if (action.code == "Start"){
                gmk.startGame(action);
            }else if (action.code == "Chess"){
                gmk.recieveChessMsg(action);
            }
        }

        gmk.startGame = function(action){
            gmt.allowPlay = true;
            if (action.detail == "White"){
                gmt.myTurn = true;
            }else{
                gmt.myTurn = false;
            }
            gmk.changeHead();
            gmk.changeTip();
        }

        gmk.recieveChessMsg = function(action){
            if (action.color == "White"){
                gmk.whiteChessBrush.drawChess(action.x, action.y);
            }else{
                gmk.blackChessBrush.drawChess(action.x, action.y);
            }
            gmk.changeTip();
        }

        gmk.changeTip = function(){
            if (myTurn == true){
                document.getElementById("tips").innerHTML = "这是你的回合";
            }else{
                document.getElementById("tips").innerHTML = "等待对方下棋";
            }
        }

        gmk.changeHead = function(){
            if (myTurn == false){
                document.getElementById("head").innerHTML = "你的房间号为" + roomId + "你是黑色";
            }else{
                document.getElementById("head").innerHTML = "你的房间号为" + roomId + "你是白色";
            }
        }

        return gmk;
    }
}

var Step = {
    createNew : function(x, y, color){
        var step = {}
        step.x = x;
        step.y = y;
        step.color = color;
        return step;
    }
}

var ChessBrush = {
    createNew : function(ct, pic){
        var cb = {};
        cb.ct = ct;
        cb.pic = new Image();
        cb.pic.scr = pic;
        cb.drawChess = function(x, y){
            cb.ct.drawImage(cb.pic, x, y);
        }
        return cb;
    }
}

var BoardBrush = {
    createNew : function(ct){
        bb = {};
        bb.ct = ct;
        bb.show = function(){
            ct.clearRect(0, 0, 640, 640);
            ct.fillStyle = "#996600";
            ct.fillRect(0, 0, 640, 640);
            ct.fillStyle = "#CCFFFF";
            ct.strokeStyle = "white";
            for (var i = 0; i <= 640; i += 40){
                ct.beginPath();
                ct.moveTo(0, i);
                ct.lineTo(640, i);
                ct.closePath();
                ct.stroke();

                ct.beginPath();
                ct.moveTo(i, 0);
                ct.lineTo(i, 640);
                ct.closePath();
                ct.stroke();
            }
        }
        return bb;
    }
}

var Stack = {
    createNew : function(){
        var stack = {};
        stack.elements = [];
        stack.size = function(){
            var len = stack.elements.length;
            return stack.elements.length;
        };
        stack.push = function(e){
            stack.elements.push(e);
        };
        stack.pop = function(){
            return stack.elements.pop();
        };
        stack.isEmpty = function(){
            return stack.size == 0;
        };
        stack.clear = function(){
            return stack.elements = [];
        };
        stack.top = function(){
            if (stack.isEmpty == true) return null;
            return stack.elements[stack.size() - 1];
        };

        return stack;
    }
}

var Communicator = {
    createNew : function(hanlder){
        net = {};
        net.hanlder = hanlder;
        try{
            net.webSocket = new WebSocket(host);
            net.webSocket.onopen = function(){
                alert("链接成功");
            }
        }catch(a){
            return null;
        }

        net.parse = function(){
            result = JSON.parse(data);
        }

        net.webSocket.onmessage = function(event){
            action = net.parse(event.data);
            if (net.hanlder && net.hanlder == "function"){
                net.hanlder(action);
            }
        }

    }
}