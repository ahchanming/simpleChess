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

        //房间号
        gmk.roomId = prompt("请输入房间号", "default");
        gmk.host = "ws://" + document.location.host + "/chess/chess?roomId=" + gmk.roomId;

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

        gmk.communicator = Communicator.createNew(gmk.host, gmk.hanlderEvent);

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

        gmk.img_b = new Image();
        gmk.img_b.src = "../images/b.png";

        gmk.img_w = new Image();
        gmk.img_w.src = "../images/w.png";

        gmk.img_b_now = new Image();
        gmk.img_b_now.src = "../images/b_now.png";

        gmk.img_w_now = new Image();
        gmk.img_w_now.src = "../images/w_now.png";

        gmk.whiteChessBrush = ChessBrush.createNew(gmk.ct, gmk.img_w);
        gmk.blackChessBrush = ChessBrush.createNew(gmk.ct, gmk.img_b);
        gmk.whiteNowChessBrush = ChessBrush.createNew(gmk.ct, gmk.img_w_now);
        gmk.blackNowChessBrush = ChessBrush.createNew(gmk.ct, gmk.img_b_now);

        gmk.last_x = -1;
        gmk.last_y = -1;

        //棋盘刷子
        gmk.boardBrush = BoardBrush.createNew(gmk.ct);

        //准备状态
        gmk.isReady = false;

        //我的颜色
        gmk.myColor = null;

        gmk.init = function(){
            for (var x = 0; x < 15; ++x){
                gmk.chessData[x] = new Array(15);
                for (var y = 0; y < 15; ++y){
                    gmk.chessData[x][y] = 0;
                }
            }
            gmk.last_x = -1;
            gmk.last_y = -1;
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

        gmk.getRelativeX = function(x){
            return x * 40 + 20;
        }

        gmk.getRelativeY = function(y){
            return y * 40 + 20;
        }

        gmk.play = function(e){
            //alert("play hahaha");
            if (!gmk.allowPlay){
                alert("游戏尚未开始");
                return;
            }

            if (!gmk.myTurn){
                alert("不是你的回合");
                return;
            }

            var x = gmk.getAbsoluteX(e.clientX);
            var y = gmk.getAbsoluteY(e.clientY);
            if (gmk.chessData[x][y] != 0){
                return [false, "你不能在这个位置下棋"];
            }

            gmk.sendChessMsg(gmk.myColor, x, y);
        }

        gmk.startGame = function(action){
            gmk.allowPlay = true;
            if (action.detail == "White"){
                gmk.myColor = "White";
                gmk.myTurn = true;
            }else{
                gmk.myColor = "Black";
                gmk.myTurn = false;
            }
            gmk.changeHead();
            gmk.changeTip();
        }

        gmk.recieveChessMsg = function(action){
            if (action.color == "White"){
                if (gmk.last_x != -1 && gmk.last_y != -1){
                    gmk.blackChessBrush.drawChess(gmk.getRelativeX(gmk.last_x), gmk.getRelativeY(gmk.last_y));
                }
                gmk.whiteNowChessBrush.drawChess(gmk.getRelativeX(action.x), gmk.getRelativeY(action.y));
                gmk.chessData[action.x][action.y] = 1;
            }else{
                if (gmk.last_x != -1 && gmk.last_y != -1){
                    gmk.whiteChessBrush.drawChess(gmk.getRelativeX(gmk.last_x), gmk.getRelativeY(gmk.last_y));
                }
                gmk.blackNowChessBrush.drawChess(gmk.getRelativeX(action.x), gmk.getRelativeY(action.y));
                gmk.chessData[action.x][action.y] = -1;
            }
            gmk.judge(action.x, action.y, action.color == "White" ? 1 : -1);
            gmk.last_x = action.x;
            gmk.last_y = action.y;
            gmk.myTurn = !gmk.myTurn;
            gmk.changeTip();

        }

        gmk.sendReadyMsg = function(){
            if (gmk.isReady == false){
                gmk.init();
                gmk.isReady = true;
                gmk.communicator.sendMessage("ready");
                document.getElementById("tips").innerHTML = "请耐心等待下一位玩家";
            }
        }

        gmk.sendChessMsg = function(color, x, y){
            var chessAction = new Object();
            chessAction.color = color;
            chessAction.x = x;
            chessAction.y = y;
            var chessInfoStr = JSON.stringify(chessAction);
            gmk.communicator.sendMessage("chess" + chessInfoStr);
        }

        gmk.sendOverMsg = function(){
            gmk.isReady = false;
            document.getElementById("tips").innerHTML = "请准备";
            gmk.communicator.sendMessage("over");
        }

        gmk.changeTip = function(){
            if (gmk.myTurn == true){
                document.getElementById("tips").innerHTML = "这是你的回合";
            }else{
                document.getElementById("tips").innerHTML = "等待对方下棋";
            }
        }

        gmk.changeHead = function(){
            if (gmk.myTurn == false){
                document.getElementById("head").innerHTML = "你的房间号为" + gmk.roomId + "你是黑色";
            }else{
                document.getElementById("head").innerHTML = "你的房间号为" + gmk.roomId + "你是白色";
            }
        }

        gmk.judge = function(x, y, chess){
            var count1 = 0;
            var count2 = 0;
            var count3 = 0;
            var count4 = 0;

            //左右判断
            for (var i = x; i >= 0; i--) {
                if (gmk.chessData[i][y] != chess) {
                    break;
                }
                count1++;
            }
            for (var i = x + 1; i < 15; i++) {
                if (gmk.chessData[i][y] != chess) {
                    break;
                }
                count1++;
            }
            //上下判断
            for (var i = y; i >= 0; i--) {
                if (gmk.chessData[x][i] != chess) {
                    break;
                }
                count2++;
            }
            for (var i = y + 1; i < 15; i++) {
                if (gmk.chessData[x][i] != chess) {
                    break;
                }
                count2++;
            }
            //左上右下判断
            for (var i = x, j = y; i >= 0, j >= 0; i--, j--) {
                if (gmk.chessData[i][j] != chess) {
                    break;
                }
                count3++;
            }
            for (var i = x + 1, j = y + 1; i < 15, j < 15; i++, j++) {
                if (gmk.chessData[i][j] != chess) {
                    break;
                }
                count3++;
            }
            //右上左下判断
            for (var i = x, j = y; i >= 0, j < 15; i--, j++) {
                if (gmk.chessData[i][j] != chess) {
                    break;
                }
                count4++;
            }
            for (var i = x + 1, j = y - 1; i < 15, j >= 0; i++, j--) {
                if (gmk.chessData[i][j] != chess) {
                    break;
                }
                count4++;
            }

            if (count1 >= 5 || count2 >= 5 || count3 >= 5 || count4 >= 5) {
                if (chess == 1) {
                    alert("白棋赢了");
                }
                else {
                    alert("黑棋赢了");
                }
                gmk.allowPlay = false;
                gmk.sendOverMsg();
                gmk.isReady = false;
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
        cb.pic = pic;
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
    createNew : function(host, handler){
        net = {};
        net.handler = handler;
        try{
            net.webSocket = new WebSocket(host);
            net.webSocket.onopen = function(){
                alert("链接成功");
            }
        }catch(a){
            return null;
        }

        net.parse = function(data){
            var result = JSON.parse(data);
            return result;
        }

        net.webSocket.onmessage = function(event){
            var result = net.parse(event.data);
            if (net.handler && typeof(net.handler) == "function"){
                net.handler(result);
            }
        }

        net.sendMessage = function(data){
            if (data){
                net.webSocket.send(data);
            }
        }

        return net;
    }
}