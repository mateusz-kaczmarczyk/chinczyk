using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GameBoard : MonoBehaviour
{
    public GameObject redField;
    public GameObject blueField;
    public GameObject greenField;
    public GameObject yellowField;

    private Dictionary<string, GameObject[]> bases;

    public void CreateBoard() {
        Debug.Log("Creating board");
        CreateBases();
    }

    public void DestroyBoard() {
        Debug.Log("Destroying board");
    }

    private void CreateBases() {
        Debug.Log("Create bases");
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

}
