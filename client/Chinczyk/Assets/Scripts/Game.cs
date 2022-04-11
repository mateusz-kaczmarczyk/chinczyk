using UnityEngine;
using UnityEngine.UI;
using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Concurrent;

public class Game : MonoBehaviour
{

    private string turn = string.Empty;
    private string color = string.Empty;
    private string error = string.Empty;

    public Image background;
    public Text statusText;

    public Button throwButton;
    public Button passButton;

    public Text colorText;
    public Text turnText;
    public Text thrownText;
    public Text errorText;

    public GameObject fieldBase;

    public GameObject redPawn;
    public GameObject bluePawn;
    public GameObject greenPawn;
    public GameObject yellowPawn;

    public GameObject redField;
    public GameObject blueField;
    public GameObject greenField;
    public GameObject yellowField;

    public GameObject redBase;
    public GameObject blueBase;
    public GameObject greenBase;
    public GameObject yellowBase;

    private Dictionary<int, GameObject> fields;
    private Dictionary<string, GameObject[]> pawns;
    private Dictionary<string, GameObject[]> bases;
    private Dictionary<string, GameObject[]> winBases;

    private Socket socket;
    private int port = 24000;
    private string response = string.Empty;

    public class StateObject
    {
        public Socket socket = null;
        public byte[] buffer = new byte[bufferSize];
        public const int bufferSize = 1024;
        public StringBuilder sb = new StringBuilder();
    }

    ConcurrentQueue<MoveData> queue;

    private class MoveData
    {
        public enum moveTypes { MoveToBase, MoveToWinBase, MoveToField };
        public MoveData() { }
        public MoveData(moveTypes type, int field, int pawn, string color) {
            this.moveType = type;
            this.field = field;
            this.pawn = pawn;
            this.color = color;
        }
        public moveTypes moveType;
        public int field;
        public int pawn;
        public string color;
    }

    private void ThrowClick() {
        Send("THROW\0");
    }

    private void PassClick() {
        Send("PASS\0");
    }

    private void Awake() {
        queue = new ConcurrentQueue<MoveData>();
        throwButton.onClick.AddListener(ThrowClick);
        passButton.onClick.AddListener(PassClick);
        InstantiateFields();
        InstantiateBases();
        InstantiatePawns();
        InstantiateWinBases();
    }

    private void OnApplicationQuit() {
        socket.Shutdown(SocketShutdown.Both);
        socket.Close();
    }

    private void Start() {
        StartCoroutine(WaitBeforeConnect(1f));
    }

    private void Update() {

        
        turnText.text = turn;
        errorText.text = error;

        if (Input.GetMouseButtonDown(0)) {
            Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
            RaycastHit hit;
            if (Physics.Raycast(ray, out hit)) {
                string[] parts = hit.transform.name.Split(' ');
                if (color == parts[0]) {
                    Send("MOVE " + parts[1] + "\0");
                }
            }
        }

        if (!queue.IsEmpty) {
            MoveData md;
            bool result;
            do {
                result = queue.TryDequeue(out md);
            } while (!result);
            if (md.moveType == MoveData.moveTypes.MoveToBase) pawns[md.color][md.pawn - 1].transform.position = new Vector3(bases[md.color][md.pawn - 1].transform.position.x, 1, bases[md.color][md.pawn - 1].transform.position.z);
            else if (md.moveType == MoveData.moveTypes.MoveToWinBase) pawns[md.color][md.pawn - 1].transform.position = new Vector3(winBases[md.color][md.field - 1].transform.position.x, 1, winBases[md.color][md.field - 1].transform.position.z);
            else pawns[md.color][md.pawn - 1].transform.position = new Vector3(fields[md.field].transform.position.x, 1, fields[md.field].transform.position.z);
        }
    }

    private void Connect() {
        try {
            IPAddress ipAddress = IPAddress.Parse("192.168.1.12");
            IPEndPoint remoteEP = new IPEndPoint(ipAddress, 5050);

            socket = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
            socket.BeginConnect(remoteEP, new AsyncCallback(ConnectCallback), socket);
        } catch (Exception e) {
            Debug.Log(e.ToString());
        }
    }

    IEnumerator WaitBeforeConnect(float waitTime) {
        yield return new WaitForSeconds(waitTime);
        Connect();
    }

    IEnumerator WaitBeforeQuit(float waitTime) {
        yield return new WaitForSeconds(waitTime);
        Application.Quit();
    }

    IEnumerator WinScreen(float waitTime, string color) {
        yield return new WaitForSeconds(waitTime);
        statusText.text = color + " wins";
        background.enabled = true;
        statusText.enabled = true;
        StartCoroutine(WaitBeforeQuit(3f));
    }

    private void ConnectCallback(IAsyncResult ar) {
        try {
            socket.EndConnect(ar);
            statusText.text = "Waiting for other players...";
            StateObject state = new StateObject();
            state.socket = socket;
            socket.BeginReceive(state.buffer, 0, StateObject.bufferSize, 0, new AsyncCallback(ReceiveCallback), state);
        } catch (SocketException se) {
            statusText.text = "Server unreachable";
            StartCoroutine(WaitBeforeQuit(3f));
        }
    }

    private void Send(string data) {
        byte[] byteData = Encoding.ASCII.GetBytes(data);
        socket.BeginSend(byteData, 0, byteData.Length, 0, new AsyncCallback(SendCallback), socket);
    }

    private void SendCallback(IAsyncResult ar) {
        socket.EndSend(ar);
    }

    private void ReceiveCallback(IAsyncResult ar) {
        StateObject state = (StateObject)ar.AsyncState;

        int bytesRead = socket.EndReceive(ar);
        state.sb.Append(Encoding.ASCII.GetString(state.buffer, 0, bytesRead));
        state.sb.Replace('\0', '\n');
        response = state.sb.ToString();

        string[] responseParts = response.Split('\n');
        for (int i = 0; i < responseParts.Length - 1; i++) {

            string msg = responseParts[i];

            if (msg == "GAME STARTED") {
                background.enabled = false;
                statusText.enabled = false;
            } else if (msg == "GAME CLOSED") {
                background.enabled = true;
                statusText.text = "Opponent left the game";
                statusText.enabled = true;
                StartCoroutine(WaitBeforeQuit(2f));
            }

            string[] parts = msg.Split(' ');

            if (parts[0] == "error:") {
                error = msg;
            } else {
                error = string.Empty;
            }
            if (parts[0] == "COLOR") {
                color = parts[1];
                colorText.text = "You are a " + color + " player";
                switch (color) {
                    case "Red":
                        colorText.material.color = Color.red;
                        break;
                    case "Green":
                        colorText.material.color = Color.green;
                        break;
                    case "Blue":
                        colorText.material.color = Color.blue;
                        break;
                    case "Yellow":
                        colorText.material.color = Color.yellow;
                        break;
                    default:
                        break;
                }
            } else if (parts[1] == "THROWN") thrownText.text = parts[0] + " rolled " + parts[2];
            else if (parts[0] == "TURN") {
                turn = parts[1] + " turn";
                thrownText.text = "";
            }
            else if (parts[0] == "MOVE") {
                MoveData moveData = new MoveData();
                moveData.color = parts[1];
                moveData.pawn = int.Parse(parts[3]);
                switch(parts[5]) {
                    case "Field":
                        moveData.moveType = MoveData.moveTypes.MoveToField;
                        break;
                    case "Base":
                        moveData.moveType = MoveData.moveTypes.MoveToBase;
                        break;
                    case "WinBase":
                        moveData.moveType = MoveData.moveTypes.MoveToWinBase;
                        break;
                    default:
                        break;
                }
                int field;
                int.TryParse(parts[6], out field);
                moveData.field = field;
                queue.Enqueue(moveData);
            } else if (parts[0] == "WIN") {
                StartCoroutine(WinScreen(2f, parts[1]));
            }
        }

        state.sb.Clear();
        socket.BeginReceive(state.buffer, 0, StateObject.bufferSize, 0, new AsyncCallback(ReceiveCallback), state);

    }

    private void InstantiateFields() {
        fields = new Dictionary<int, GameObject>();
        fields[0] = Instantiate(redField, new Vector3(0, 0, 0), new Quaternion(0, 0, 0, 1)); fields[0].name = "Field 0";
        fields[1] = Instantiate(fieldBase, new Vector3(2.5f, 0, 0), new Quaternion(0, 0, 0, 1)); fields[1].name = "Field 1";
        fields[2] = Instantiate(fieldBase, new Vector3(5.0f, 0, 0), new Quaternion(0, 0, 0, 1)); fields[2].name = "Field 2";
        fields[3] = Instantiate(fieldBase, new Vector3(7.5f, 0, 0), new Quaternion(0, 0, 0, 1)); fields[3].name = "Field 3";
        fields[4] = Instantiate(fieldBase, new Vector3(10f, 0, 0), new Quaternion(0, 0, 0, 1)); fields[4].name = "Field 4";
        fields[5] = Instantiate(fieldBase, new Vector3(10f, 0, 2.5f), new Quaternion(0, 0, 0, 1)); fields[5].name = "Field 5";
        fields[6] = Instantiate(fieldBase, new Vector3(10f, 0, 5f), new Quaternion(0, 0, 0, 1)); fields[6].name = "Field 6";
        fields[7] = Instantiate(fieldBase, new Vector3(10f, 0, 7.5f), new Quaternion(0, 0, 0, 1)); fields[7].name = "Field 7";
        fields[8] = Instantiate(fieldBase, new Vector3(10f, 0, 10f), new Quaternion(0, 0, 0, 1)); fields[8].name = "Field 8";
        fields[9] = Instantiate(fieldBase, new Vector3(12.5f, 0, 10f), new Quaternion(0, 0, 0, 1)); fields[9].name = "Field 9";
        fields[10] = Instantiate(greenField, new Vector3(15f, 0, 10f), new Quaternion(0, 0, 0, 1)); fields[10].name = "Field 10";
        fields[11] = Instantiate(fieldBase, new Vector3(15f, 0, 7.5f), new Quaternion(0, 0, 0, 1)); fields[11].name = "Field 11";
        fields[12] = Instantiate(fieldBase, new Vector3(15f, 0, 5f), new Quaternion(0, 0, 0, 1)); fields[12].name = "Field 12";
        fields[13] = Instantiate(fieldBase, new Vector3(15f, 0, 2.5f), new Quaternion(0, 0, 0, 1)); fields[13].name = "Field 13";
        fields[14] = Instantiate(fieldBase, new Vector3(15f, 0, 0f), new Quaternion(0, 0, 0, 1)); fields[14].name = "Field 14";
        fields[15] = Instantiate(fieldBase, new Vector3(17.5f, 0, 0f), new Quaternion(0, 0, 0, 1)); fields[15].name = "Field 15";
        fields[16] = Instantiate(fieldBase, new Vector3(20f, 0, 0f), new Quaternion(0, 0, 0, 1)); fields[16].name = "Field 16";
        fields[17] = Instantiate(fieldBase, new Vector3(22.5f, 0, 0f), new Quaternion(0, 0, 0, 1)); fields[17].name = "Field 17";
        fields[18] = Instantiate(fieldBase, new Vector3(25f, 0, 0f), new Quaternion(0, 0, 0, 1)); fields[18].name = "Field 18";
        fields[19] = Instantiate(fieldBase, new Vector3(25f, 0, -2.5f), new Quaternion(0, 0, 0, 1)); fields[19].name = "Field 19";
        fields[20] = Instantiate(blueField, new Vector3(25f, 0, -5f), new Quaternion(0, 0, 0, 1)); fields[20].name = "Field 20";
        fields[21] = Instantiate(fieldBase, new Vector3(22.5f, 0, -5f), new Quaternion(0, 0, 0, 1)); fields[21].name = "Field 21";
        fields[22] = Instantiate(fieldBase, new Vector3(20f, 0, -5f), new Quaternion(0, 0, 0, 1)); fields[22].name = "Field 22";
        fields[23] = Instantiate(fieldBase, new Vector3(17.5f, 0, -5f), new Quaternion(0, 0, 0, 1)); fields[23].name = "Field 23";
        fields[24] = Instantiate(fieldBase, new Vector3(15f, 0, -5f), new Quaternion(0, 0, 0, 1)); fields[24].name = "Field 24";
        fields[25] = Instantiate(fieldBase, new Vector3(15f, 0, -7.5f), new Quaternion(0, 0, 0, 1)); fields[25].name = "Field 25";
        fields[26] = Instantiate(fieldBase, new Vector3(15f, 0, -10f), new Quaternion(0, 0, 0, 1)); fields[26].name = "Field 26";
        fields[27] = Instantiate(fieldBase, new Vector3(15f, 0, -12.5f), new Quaternion(0, 0, 0, 1)); fields[27].name = "Field 27";
        fields[28] = Instantiate(fieldBase, new Vector3(15f, 0, -15f), new Quaternion(0, 0, 0, 1)); fields[28].name = "Field 28";
        fields[29] = Instantiate(fieldBase, new Vector3(12.5f, 0, -15f), new Quaternion(0, 0, 0, 1)); fields[29].name = "Field 29";
        fields[30] = Instantiate(yellowField, new Vector3(10f, 0, -15f), new Quaternion(0, 0, 0, 1)); fields[30].name = "Field 30";
        fields[31] = Instantiate(fieldBase, new Vector3(10f, 0, -12.5f), new Quaternion(0, 0, 0, 1)); fields[31].name = "Field 31";
        fields[32] = Instantiate(fieldBase, new Vector3(10f, 0, -10f), new Quaternion(0, 0, 0, 1)); fields[32].name = "Field 32";
        fields[33] = Instantiate(fieldBase, new Vector3(10f, 0, -7.5f), new Quaternion(0, 0, 0, 1)); fields[33].name = "Field 33";
        fields[34] = Instantiate(fieldBase, new Vector3(10f, 0, -5f), new Quaternion(0, 0, 0, 1)); fields[34].name = "Field 34";
        fields[35] = Instantiate(fieldBase, new Vector3(7.5f, 0, -5f), new Quaternion(0, 0, 0, 1)); fields[35].name = "Field 35";
        fields[36] = Instantiate(fieldBase, new Vector3(5f, 0, -5f), new Quaternion(0, 0, 0, 1)); fields[36].name = "Field 36";
        fields[37] = Instantiate(fieldBase, new Vector3(2.5f, 0, -5f), new Quaternion(0, 0, 0, 1)); fields[37].name = "Field 37";
        fields[38] = Instantiate(fieldBase, new Vector3(0f, 0, -5f), new Quaternion(0, 0, 0, 1)); fields[38].name = "Field 38";
        fields[39] = Instantiate(fieldBase, new Vector3(0f, 0, -2.5f), new Quaternion(0, 0, 0, 1)); fields[39].name = "Field 39";
    }

    private void InstantiateBases() {
        bases = new Dictionary<string, GameObject[]>();
        bases["Red"] = new GameObject[4];
        bases["Blue"] = new GameObject[4];
        bases["Green"] = new GameObject[4];
        bases["Yellow"] = new GameObject[4];

        bases["Red"][0] = Instantiate(redField, new Vector3(0, 0, 7.5f), new Quaternion(0, 0, 0, 1)); bases["Red"][0].name = "Red base 1";
        bases["Red"][1] = Instantiate(redField, new Vector3(0, 0, 10f), new Quaternion(0, 0, 0, 1)); bases["Red"][1].name = "Red base 2";
        bases["Red"][2] = Instantiate(redField, new Vector3(2.5f, 0, 7.5f), new Quaternion(0, 0, 0, 1)); bases["Red"][2].name = "Red base 3";
        bases["Red"][3] = Instantiate(redField, new Vector3(2.5f, 0, 10f), new Quaternion(0, 0, 0, 1)); bases["Red"][3].name = "Red base 4";

        bases["Blue"][0] = Instantiate(blueField, new Vector3(22.5f, 0, -15f), new Quaternion(0, 0, 0, 1)); bases["Blue"][0].name = "Blue base 1";
        bases["Blue"][1] = Instantiate(blueField, new Vector3(22.5f, 0, -12.5f), new Quaternion(0, 0, 0, 1)); bases["Blue"][1].name = "Blue base 2";
        bases["Blue"][2] = Instantiate(blueField, new Vector3(25f, 0, -15f), new Quaternion(0, 0, 0, 1)); bases["Blue"][2].name = "Blue base 3";
        bases["Blue"][3] = Instantiate(blueField, new Vector3(25f, 0, -12.5f), new Quaternion(0, 0, 0, 1)); bases["Blue"][3].name = "Blue base 4";

        bases["Green"][0] = Instantiate(greenField, new Vector3(22.5f, 0, 7.5f), new Quaternion(0, 0, 0, 1)); bases["Green"][0].name = "Green base 1";
        bases["Green"][1] = Instantiate(greenField, new Vector3(22.5f, 0, 10f), new Quaternion(0, 0, 0, 1)); bases["Green"][1].name = "Green base 2";
        bases["Green"][2] = Instantiate(greenField, new Vector3(25f, 0, 7.5f), new Quaternion(0, 0, 0, 1)); bases["Green"][2].name = "Green base 3";
        bases["Green"][3] = Instantiate(greenField, new Vector3(25f, 0, 10f), new Quaternion(0, 0, 0, 1)); bases["Green"][3].name = "Green base 4";

        bases["Yellow"][0] = Instantiate(yellowField, new Vector3(0f, 0, -15f), new Quaternion(0, 0, 0, 1)); bases["Yellow"][0].name = "Yellow base 1";
        bases["Yellow"][1] = Instantiate(yellowField, new Vector3(0f, 0, -12.5f), new Quaternion(0, 0, 0, 1)); bases["Yellow"][1].name = "Yellow base 2";
        bases["Yellow"][2] = Instantiate(yellowField, new Vector3(2.5f, 0, -15f), new Quaternion(0, 0, 0, 1)); bases["Yellow"][2].name = "Yellow base 3";
        bases["Yellow"][3] = Instantiate(yellowField, new Vector3(2.5f, 0, -12.5f), new Quaternion(0, 0, 0, 1)); bases["Yellow"][3].name = "Yellow base 4";

    }

    private void InstantiatePawns() {
        pawns = new Dictionary<string, GameObject[]>();
        pawns["Red"] = new GameObject[4];
        pawns["Blue"] = new GameObject[4];
        pawns["Green"] = new GameObject[4];
        pawns["Yellow"] = new GameObject[4];

        for (int i = 0; i < 4; i++) {
            pawns["Red"][i] = Instantiate(redPawn, bases["Red"][i].transform.position, new Quaternion(0, 0, 0, 1)); pawns["Red"][i].name = "Red " + (i + 1).ToString();
            pawns["Blue"][i] = Instantiate(bluePawn, bases["Blue"][i].transform.position, new Quaternion(0, 0, 0, 1)); pawns["Blue"][i].name = "Blue " + (i + 1).ToString();
            pawns["Green"][i] = Instantiate(greenPawn, bases["Green"][i].transform.position, new Quaternion(0, 0, 0, 1)); pawns["Green"][i].name = "Green " + (i + 1).ToString();
            pawns["Yellow"][i] = Instantiate(yellowPawn, bases["Yellow"][i].transform.position, new Quaternion(0, 0, 0, 1)); pawns["Yellow"][i].name = "Yellow " + (i + 1).ToString();
        }
    }

    private void InstantiateWinBases() {
        winBases = new Dictionary<string, GameObject[]>();
        winBases["Red"] = new GameObject[4];
        winBases["Blue"] = new GameObject[4];
        winBases["Green"] = new GameObject[4];
        winBases["Yellow"] = new GameObject[4];

        for (int i = 0; i < 4; i++) {
            winBases["Red"][i] = Instantiate(redField, new Vector3(2.5f + (i * 2.5f), 0, -2.5f), new Quaternion(0, 0, 0, 1)); winBases["Red"][i].name = "Red WinBase " + i.ToString();
            winBases["Blue"][i] = Instantiate(blueField, new Vector3(22.5f - (i * 2.5f), 0, -2.5f), new Quaternion(0, 0, 0, 1)); winBases["Blue"][i].name = "Blue WinBase " + i.ToString();
            winBases["Green"][i] = Instantiate(greenField, new Vector3(12.5f, 0, 7.5f - (i * 2.5f)), new Quaternion(0, 0, 0, 1)); winBases["Green"][i].name = "Green WinBase " + i.ToString();
            winBases["Yellow"][i] = Instantiate(yellowField, new Vector3(12.5f, 0, -12.5f + (i * 2.5f)), new Quaternion(0, 0, 0, 1)); winBases["Yellow"][i].name = "Yellow WinBase " + i.ToString();
        }
    }
}
