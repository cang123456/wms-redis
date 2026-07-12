<script>
import Header from "@/components/Header.vue";
import Aside from "@/components/Aside.vue";

export default {
  name: "GoodsManage",
  data()
  {
    return {
      // 图片
      picturePath:'',
      pictureshow:false,
      imgUrl:'',
      // 上传请求头（携带Token）
      uploadHeaders: {
        'token': sessionStorage.getItem('token') || ''
      },

      // 后端返回的数据
      tableData: [],//
      storageData:[],
      goodsTypeData:[],

      pageSize:8,
      pageNum:1,
      total:0,
      name:'',
      storage:'',
      goodsType:'',
      buy_price:'',
      sell_price:'',

      centerDialogVisible:false,
      form:{
        name:'',
        imgUrl:'',
        storage:'',
        goodsType:'',
        count:'',
        remark:'',
        buy_price:'',
        sell_price:'',
      },
      rules: {
        name: [
          {required: true, message: '请输入物品名', trigger: 'blur'},
        ],
        goodsType:[
            {required: true, message: '请选择物品分类', trigger: 'blur'},
        ],
        storage:[
            {required: true, message: '请选择仓库', trigger: 'blur'},
        ],
        count:[
            {required: true, message: '请输入物品数量', trigger: 'blur'},
            {pattern: /^([1-9][0-9]*){1,4}$/,message: '数量必须为正整数字',trigger: "blur"},
            // {validator:checkCount,trigger: 'blur'}
        ],
        buy_price: [
          { required: true, message: '请输入进货价', trigger: 'blur' },
          { pattern: /^(0\.\d{1,2}|[1-9]\d*(\.\d{1,2})?)$/, message: '价格必须大于0，最多支持两位小数', trigger: 'blur' }
        ],
        sell_price: [
          { required: true, message: '请输入零售价', trigger: 'blur' },
          { pattern: /^(0\.\d{1,2}|[1-9]\d*(\.\d{1,2})?)$/, message: '价格必须大于0，最多支持两位小数', trigger: 'blur' }
        ],
      }
    }
  },
  computed: {
    // 用计算属性动态生成上传地址，100% 安全
    uploadUrl() {
      // 注意：这里的端口要和你后端一致！
      return this.$httpUrl + '/goods/uploadPicture'
    },
    baseUrl(){
      return this.$httpUrl
    }
  },
  methods:{
    // 图片
      // 上传前校验
    beforeUpload(file) {
      const isImage = file.type.indexOf('image/') !== -1
      const isLt5M = file.size / 1024 / 1024 < 5

      if (!isImage) {
        this.$message.error('只能上传图片！')
        return false
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过 5MB!')
        return false
      }
      return true
    },
      // 上传成功后回调
    handleUploadSuccess(response) {
      console.log("上传成功", response)
      // 后端返回 { imgUrl: "xxx" }
      if (response && response.imgUrl) {
        this.picturePath = this.$httpUrl + response.imgUrl
        this.form.imgUrl = response.imgUrl  // 把图片地址存入表单
        this.pictureshow = true
        this.$message.success("图片上传成功！")
      }
    },
          // 上传失败
    handleUploadError() {
      this.$message.error("图片上传失败！")
    },
    // 图片结束


    del(id){
      console.log(id)
      this.$axios.get(this.$httpUrl+'/goods/delete?id='+id).then(res=>res.data).then(res=>{
        console.log(res)
        if(res.code === 200){
          this.$message({
            message:'删除成功',
            type:'success'
          })
          this.loadPost()
        } else {
          this.$message({
            message:'删除失败',
            type: 'warning'
          })
        }
      })
    },
    mod(row){
      console.log(row)
      this.form.id = row.id
      this.form.storage = row.storage
      this.form.goodsType = row.goodsType
      this.form.count = row.count
      this.form.name = row.name
      this.form.remark = row.remark
      this.form.imgUrl = row.imgUrl
      
      this.centerDialogVisible = true
    },
    resetForm() {
      this.$refs.form.resetFields();
    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
      this.pageSize=val
      this.loadPost()
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
      this.pageNum = val;
      this.loadPost()
    },
    resetParam(){
      this.name = ''
      this.goodsType = ''
      this.storage = ''
    },
    loadPost(){
      this.$axios.post(this.$httpUrl+'/goods/listPage',{
        pageSize:this.pageSize,
        pageNum:this.pageNum,
        param:{
          name:this.name,
          goodsType:this.goodsType+'',
          storage:this.storage+''
        }
      }).then(res=>res.data).then(res=>{
        console.log(res)
        if(res.code === 200){
          this.tableData = res.data
          this.total = res.total
        } else {
          alert("获取失败")
        }
      })
    },
    formatStorage(row){
      if(!this.storageData) return '';
      const matchedItem = this.storageData.find(item => {
        return row.storage === item.id;
      })
      return matchedItem ? matchedItem.name : '';
    },
    formatGoodstype(row){
      if(!this.goodsTypeData) return '';
      const matchedItem = this.goodsTypeData.find(item => {
        return row.goodsType === item.id;
      })
      return matchedItem ? matchedItem.name : '';
    },
    loadStorage(){
      this.$axios.get(this.$httpUrl+'/storage/list').then(res=>res.data).then(res=>{
        if(res.code === 200){
          this.storageData = res.data
        } else {
          alert("获取失败")
        }
      })
    },
    loadGoodstype(){
      this.$axios.get(this.$httpUrl+'/goodstype/list').then(res=>res.data).then(res=>{
        if(res.code === 200){
          this.goodsTypeData = res.data
        } else {
          alert("获取失败")
        }
      })
    },
    add(){
      this.centerDialogVisible = true
      this.$nextTick(()=>{
        this.resetForm();
      })
    },
    doSave(){
      this.$axios.post(this.$httpUrl+'/goods/save',this.form).then(res=>res.data).then(res=>{
        console.log(res)
        if(res.code === 200){
          this.$message({
            message:'操作成功',
            type:'success'
          })
          this.centerDialogVisible = false
          this.loadPost()
          this.resetForm()
        } else {
          this.$message({
            message:'操作失败',
            type: 'warning'
          })
        }
      })
    },
    doMod(){
      this.$axios.post(this.$httpUrl+'/goods/update',this.form).then(res=>res.data).then(res=>{
        console.log(res)
        if(res.code === 200){
          this.tableData = res.data
          this.total = res.total
          this.$message.success("修改成功！")
          this.loadPost()
        } else {
          this.$message.error("修改失败！")
        }
      })
    },
    sava(){
      this.$refs.form.validate((valid) => {
        if (valid) {//表单校验是否通过
          if(this.form.id){
            this.doMod();
          }else {
            this.doSave();
          }
        } else {
          alert("添加失败，请输入正确的格式！")
          return false;
        }
      });
    },
    checkCount(rule,value,callback){

    },
    headerStyle(){
      // console.log(Index)
      return 'background:rgb(245, 246, 247);text-align:center;'
    }
  },
  beforeMount() {
    // this.loadGet();
    this.loadPost();
    this.loadStorage();
    this.loadGoodstype();
  }
};

</script>

<template>
  <div>
    <div style="margin-bottom: 5px;">
<!--      主题的首部，选择框-->
      <el-input v-model="name" placeholder="请输入物品名称" suffix-icon="el-icon-search" style="width: 200px;"
                @keyup.enter.native="loadPost"></el-input>

      <el-select style="margin-left: 10px;"  v-model="storage" clearable placeholder="请选择仓库">
        <el-option
            v-for="item in storageData"
            :key="item.id"
            :label="item.name"
            :value="item.id">
        </el-option>
      </el-select>
      <el-select style="margin-left: 10px;" v-model="goodsType" clearable placeholder="请选择分类">
        <el-option
            v-for="item in goodsTypeData"
            :key="item.id"
            :label="item.name"
            :value="item.id">
        </el-option>
      </el-select>
      <el-button type="primary" style="margin-left: 5px;" @click="loadPost">查询</el-button>
      <el-button type="success" @click="resetParam">重置</el-button>
      <el-button type="primary" @click="add">新增</el-button>
    </div>
    <el-table :data="tableData"
              :header-cell-style="headerStyle"
              border

    >
      <el-table-column fixed prop="id" label="ID" min-width="60">
      </el-table-column>
      <el-table-column prop="name" label="物品" min-width="80">
      </el-table-column>
      <el-table-column prop="imgUrl" label="图片" min-width="80" >
        <template slot-scope="scope">
          <img v-if="scope.row.imgUrl"
               v-bind:src="$httpUrl + scope.row.imgUrl"
               style="width: 80px; height: 80px; object-fit: cover;"
              >
          <span v-else style="color: #999;">无图片</span>
        </template>
<!--        <img v-bind:src="baseUrl+imgUrl" id="img-preview1">-->
      </el-table-column>
      <el-table-column prop="storage" label="仓库" min-width="60" :formatter="formatStorage">
      </el-table-column>
      <el-table-column prop="goodsType" label="物品分类" min-width="60" :formatter="formatGoodstype">
      </el-table-column>
      <el-table-column prop="count" label="数量" min-width="60">
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="180">
      </el-table-column>

      <el-table-column align="center" fixed="right" prop="remark" label="操作" min-width="180" >
        <template slot-scope="scope">
          <el-button size="small"  type="primary" @click="mod(scope.row)">编辑</el-button>
          <el-popconfirm
              title="确定删除吗？"
              @confirm="del(scope.row.id)"
              style="margin-left: 10px;"
          >
            <el-button slot="reference" size="small"  type="danger">出库</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        style="text-align: center;margin-bottom: 15px;"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[2,5,13,17]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>

    <el-dialog
        title="提示"
        :visible.sync="centerDialogVisible"
        width="30%"
        center>
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="物品名称" prop="name">
          <el-col :span="20">
            <el-input v-model="form.name"></el-input>
          </el-col>
        </el-form-item>
<!--        原来填的表格-->
<!--        <el-form-item label="仓库" prop="storage">-->
<!--          <el-col :span="20">-->
<!--            <el-input v-model="form.storage"></el-input>-->
<!--          </el-col>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="分类" prop="goodsType">-->
<!--          <el-col :span="20">-->
<!--            <el-input v-model="form.goodsType"></el-input>-->
<!--          </el-col>-->
<!--        </el-form-item>-->
        <el-form-item label="仓库" prop="storage">
          <el-col :span="20">
            <el-select style="width: 100%;"  v-model="form.storage" clearable placeholder="请选择仓库">
              <el-option
                  v-for="item in storageData"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
              </el-option>
            </el-select>
          </el-col>
        </el-form-item>
        <el-form-item label="分类" prop="goodsType">
          <el-col :span="20">
            <el-select style="width: 100%;" v-model="form.goodsType" clearable placeholder="请选择分类">
              <el-option
                  v-for="item in goodsTypeData"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
              </el-option>
            </el-select>
          </el-col>
        </el-form-item>
        <el-form-item label="数量" prop="count">
          <el-col :span="20">
            <el-input v-model="form.count"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="成本" prop="buy_price">
          <el-col :span="20">
            <el-input v-model="form.buy_price"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="建议零售价" prop="sell_price">
          <el-col :span="20">
            <el-input v-model="form.sell_price"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-col :span="20">
            <el-input type="textarea" v-model="form.remark"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="图片" prop="imgUrl">
          <div id="third-area">
            <div id="upload-area">
              <el-upload
                  :action="uploadUrl"
                  :show-file-list="false"
                  :before-upload="beforeUpload"
                  :on-success="handleUploadSuccess"
                  :on-error="handleUploadError"
                  :headers="uploadHeaders"
                  name="image"
              >
                <el-button class="upload-button">上传商品图片</el-button>
                <p id="upload-tip">支持大小不超过 5M 的 jpg、jpeg、png 图片</p>
              </el-upload>
            </div>
            <div id="show-picture">
              <img v-bind:src="picturePath">
            </div>
            <div id="picture-preview" v-if="pictureshow">
              <p id="preview-title">效果预览</p>
              <p id="preview-description">你上传的图片会自动生成以下尺寸, 请注意是否清晰。</p>
              <img v-bind:src="picturePath" id="img-preview1">
              <img v-bind:src="picturePath" id="img-preview2">
            </div>
<!--            <button id="picture-submit" @click="uploadPicture()">保存</button>-->
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="sava">确 定</el-button>
      </span>
    </el-dialog>
  </div>



</template>

<style scoped>
/* 图片上传 */
#third-area {
  /* width: 830px; */
/*margin-left: 20px;*/
  height: 480px;

  margin-top: 20px;
  padding-top: 10px;
  background-color: white;
  border-radius: 10px;
}
#upload-area {
  width: 500px;
  height: 50px;
  margin-left: 70px;
  margin-top: 20px;
}
.upload-button {
  width: 100px;
  height: 35px;
  background-color: rgb(250, 250, 250);
  color: black;
  border: 1px rgb(198, 194, 194) solid;
}
#upload-button:hover {
  background-color: rgb(255, 255, 255);
}
#upload-tip {
  font-size: 14px;
  margin-left: 20px;
}
#show-picture {
  width: 400px;
  height: 300px;
  background-color: rgb(238, 232, 232);
  margin-left: 70px;
  margin-top: 10px;
  float: left;
}
#show-picture img {
  width: 300px;
  height: 225px;
  margin-left: 50px;
  margin-top: 35px;
}
#picture-preview {
  width: 250px;
  height: 300px;
  background-color: white;
  margin-left: 500px;
  margin-top: 10px;
  border-left: 1px rgb(226, 211, 211) solid;
}
#picture-submit {
  width: 80px;
  height: 35px;
  margin-left: 200px;
  margin-top: 40px;
  font-size: 16px;
  cursor: pointer;
  border: none;
  border-radius: 5px;
  color: white;
  background-color: rgb(248, 83, 37);
}
#picture-submit:hover {
  background-color: rgb(254, 103, 61);
}
#preview-title {
  font-size: 18px;
  margin-left: 28px;
}
#preview-description {
  font-size: 14px;
  margin-top: 5px;
  margin-left: 28px;
}
#img-preview1 {
  width: 110px;
  height: 110px;
  margin-top: 20px;
  margin-left: 28px;
}
#img-preview2 {
  width: 80px;
  height: 80px;
  margin-top: 20px;
  margin-left: 28px;
}
/* 图片上传 */



</style>