package com.example.maytinhqlc

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.maytinhqlc.databinding.ActivityMainBinding
import com.google.android.material.internal.ViewUtils.showKeyboard

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kết nối layout với activity bằng ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Thiết lập toolbar làm ActionBar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "MÁY TÍNH QUẢN LÝ CA"

        // Đảm bảo bố cục không bị che bởi thanh điều hướng hay status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        // ---- GIÁ TRỊ MẶC ĐỊNH CHO CÁC Ô NHẬP ----
//        binding.edtNhapTheTich.setText("0")           // Mặc định thể tích
//        binding.edtNhapSoMayChiet.setText("0")        // Mặc định số máy chiết
//        binding.edtSoDauPhieu.setText("0")             // Mặc định số đầu phiếu
//        binding.edtSoKetPhieu.setText("0")              // Mặc định số kết phiếu

        // ---------------------- CẤU HÌNH CÁC Ô NHẬP ---------------------- //

        // Ô nhập thể tích
        binding.edtNhapTheTich.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.edtNhapTheTich.hint = ""
                binding.edtNhapTheTich.text.clear()
            } else {
                binding.edtNhapTheTich.hint = "Thế tích Bia"
                tinhSoChai()
            }
        }

        // Ô nhập số máy chiết
        binding.edtNhapSoMayChiet.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.edtNhapSoMayChiet.hint = ""
                binding.edtNhapSoMayChiet.text.clear()
            } else {
                binding.edtNhapSoMayChiet.hint = "Số máy Chiết"
                tinhSoPallet()
            }
        }

        // Ô nhập số đầu phiếu
        binding.edtSoDauPhieu.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.edtSoDauPhieu.hint = ""
                binding.edtSoDauPhieu.text.clear()
            } else {
                binding.edtSoDauPhieu.hint = "Số đầu Phiếu"
                nhapPhieu()
            }
        }

        // Ô số kết phiếu - chỉ xoá khi focus
        binding.edtSoKetPhieu.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.edtSoKetPhieu.text.clear()
            }
        }

        // ---------------------- TỰ ĐỘNG TÍNH TOÁN KHI GÕ ---------------------- //

        binding.edtNhapTheTich.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = tinhSoChai()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edtNhapSoMayChiet.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = tinhSoPallet()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edtSoDauPhieu.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = nhapPhieu()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.edtSoKetPhieu.doAfterTextChanged {
            nhapPhieu()
        }

        binding.btnReset.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc muốn xoá toàn bộ dữ liệu không?")
                .setPositiveButton("Đồng ý") { _, _ ->
                    xoaTatCaDuLieu()
                }
                .setNegativeButton("Huỷ", null)
                .show()
        }


    }

    private fun xoaTatCaDuLieu() {
        binding.edtNhapTheTich.text.clear()
        binding.edtNhapSoMayChiet.text.clear()
        binding.edtSoDauPhieu.text.clear()
        binding.edtSoKetPhieu.text.clear()

        // Reset TextView về 0 hoặc trống
        binding.txtSoChai.text = "0"
        binding.txtNgatRong.text = "0"
        binding.txtSoPallet.text = "0"
        binding.txtSoKetTP.text = "0"
        binding.txtMCPhieu.text = "0"
        binding.txtCuoiPhieu.text = "0"

        // Optional: Đưa focus về ô đầu tiên
        binding.edtNhapTheTich.requestFocus()
    }

    // ---------------------- CÁC HÀM XỬ LÝ TÍNH TOÁN ---------------------- //

    /**
     * Tính toán số lượng chai và số ngắt rồng từ thể tích nhập vào
     * - 1 chai có thể tích 0.00355 m3
     * - Nếu vượt 19000 chai, chia thành từng cụm 1000 để biết số ngắt
     */
    private fun tinhSoChai() {
        val theTich = binding.edtNhapTheTich.text.toString().toDoubleOrNull() ?: 0.0
        val soChai = theTich / 0.00355
        val soNgatRong = ((soChai - 19000) / 1000).toInt() * 1000

        val soChaiText = if (soChai > 0) "% ,d".format(soChai.toInt()) else "0"
        val soNgatRongText = if (soNgatRong > 0) "% ,d".format(soNgatRong.toInt()) else "0"

        binding.txtSoChai.text = soChaiText
        binding.txtNgatRong.text = soNgatRongText
    }

    /**
     * Tính số pallet và kết thành phẩm từ số máy chiết
     * - 1 pallet = 1080
     * - 1 pallet = 54 kết
     * - Tự động set kết quả vào EditText "số kết phiếu"
     */
    private fun tinhSoPallet() {
        val soMayChiet = binding.edtNhapSoMayChiet.text.toString().toDoubleOrNull() ?: 0.0
        val soPallet = soMayChiet / 1080
        val soKetTP = soPallet * 54

        val soPalletText = if (soPallet > 0) "% ,d".format(soPallet.toInt()) else "0"
        val soKetTPText = if (soKetTP > 0) "% ,d".format(soKetTP.toInt()) else "0"

        binding.txtSoPallet.text = soPalletText
        binding.txtSoKetTP.text = soKetTPText
        binding.edtSoKetPhieu.setText(soKetTP.toInt().toString())
        nhapPhieu()
    }

    /**
     * Tính số cuối phiếu và số MC phiếu từ đầu phiếu và kết phiếu
     * - Số cuối phiếu = đầu phiếu + số kết
     * - Số MC phiếu = số kết * 20
     */

    private fun nhapPhieu() {
        val soKetPhieu = binding.edtSoKetPhieu.text.toString().toDoubleOrNull() ?: 0.0
        val soDauPhieu = binding.edtSoDauPhieu.text.toString().toDoubleOrNull() ?: 0.0

        val soCuoiPhieu = soDauPhieu + soKetPhieu
        val soMcPhieu = soKetPhieu * 20

        val soCuoiPhieuTxt = if (soCuoiPhieu > 0) "% ,d".format(soCuoiPhieu.toInt()) else "0"
        val soMcPhieuTxt = if (soMcPhieu > 0) "% ,d".format(soMcPhieu.toInt()) else "0"
//        val soKetPhieuTxt = if (soKetPhieu > 0) "% ,d".format(soKetPhieu.toInt()) else "0"
        binding.txtCuoiPhieu.text = soCuoiPhieuTxt
        binding.txtMCPhieu.text = soMcPhieuTxt
//        binding.edtSoKetPhieu.setText(soKetPhieuTxt.toInt().toString())
    }
}
