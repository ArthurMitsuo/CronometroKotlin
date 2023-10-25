class PersonModel {
    private var name: String = ""
    private lateinit var time: List<String>;
    constructor(name:String , time: List<String>){
        this.name = name;
        this.time = time;
    }

    //Retorna a própria classe instanciada como objeto
    fun getPerson(): PersonModel{
        return this;
    }
    fun getName(): String{
        return this.name;
    }

    fun getTimes(): List<String>{
        return this.time;
    }
}