using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class GUIcontroller : MonoBehaviour {

    public Button testButton;
    public Button test2Button;

    void Start() {
        testButton.onClick.AddListener(testClick);
        test2Button.onClick.AddListener(test2Click);
    }

    private void testClick() {
        Debug.Log("Button clicked");
        FindObjectOfType<GameBoard>().CreateBoard();
    }

    private void test2Click() {
        Debug.Log("Button2 clicked");
        FindObjectOfType<GameBoard>().DestroyBoard();
    }
}
